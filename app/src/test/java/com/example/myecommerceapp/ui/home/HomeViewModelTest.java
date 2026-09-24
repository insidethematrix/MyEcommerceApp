package com.example.myecommerceapp.ui.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.myecommerceapp.TestData;
import com.example.myecommerceapp.data.model.Clothing;
import com.example.myecommerceapp.data.model.Electronics;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.fakes.FakeProductRepository;
import com.example.myecommerceapp.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeProductRepository repository;
    private HomeViewModel viewModel;
    private final List<Resource<List<Product>>> emissions = new ArrayList<>();

    @Before
    public void setUp() {
        repository = new FakeProductRepository();
        viewModel = new HomeViewModel(repository);
        viewModel.getProducts().observeForever(emissions::add);
    }

    @Test
    public void loadingState_isPassedThrough() {
        assertEquals(Resource.Status.LOADING, latest().getStatus());
    }

    @Test
    public void withoutFilters_showsWholeCatalog() {
        repository.emit(catalog());

        assertEquals(4, latest().getData().size());
        assertFalse(viewModel.hasActiveFilter());
    }

    @Test
    public void categoryFilter_showsOnlyThatCategory() {
        repository.emit(catalog());

        viewModel.setCategory(Clothing.CATEGORY);

        for (Product product : latest().getData()) {
            assertTrue(product instanceof Clothing);
        }
        assertEquals(2, latest().getData().size());
        assertTrue(viewModel.hasActiveFilter());
    }

    @Test
    public void query_andCategory_areCombined() {
        repository.emit(catalog());

        viewModel.setCategory(Electronics.CATEGORY);
        viewModel.setQuery("mouse");

        assertEquals(1, latest().getData().size());
        assertEquals("Wireless Mouse", latest().getData().get(0).getName());
    }

    @Test
    public void filtersSurviveCatalogUpdates() {
        viewModel.setQuery("jeans");
        repository.emit(catalog());

        assertEquals(1, latest().getData().size());
    }

    @Test
    public void noMatches_yieldsEmptyList() {
        repository.emit(catalog());

        viewModel.setQuery("xyz");

        assertEquals(Collections.emptyList(), latest().getData());
    }

    @Test
    public void seed_reportsSuccess() {
        viewModel.seed(Collections.emptyMap());

        assertEquals(Resource.Status.SUCCESS,
                viewModel.getSeedResult().getValue().getContentIfNotHandled().getStatus());
    }

    private Resource<List<Product>> latest() {
        return emissions.get(emissions.size() - 1);
    }

    private static List<Product> catalog() {
        return Arrays.asList(
                TestData.laptop(),
                TestData.electronics("e2", "Wireless Mouse", "Logi", "750", 3),
                TestData.tshirt(),
                TestData.clothing("c2", "Blue Jeans", "Levi's", "800", 3));
    }
}
