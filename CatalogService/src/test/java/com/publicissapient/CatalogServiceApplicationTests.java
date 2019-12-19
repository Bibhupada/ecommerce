package com.publicissapient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.publicissapient.catalog.CatalogController;
import com.publicissapient.catalog.dto.Product;
import com.publicissapient.catalog.service.CatalogService;

@RunWith(MockitoJUnitRunner.class) 
class CatalogServiceApplicationTests {

	@Mock
	private CatalogService catalogService;
	
	@Mock
	private CatalogController catalogController;
	
	/*
	 * @Before public void setupMock() { catalogService =
	 * mock(CatalogService.class); catalogController =
	 * mock(CatalogController.class); }
	 */
	
	  @Before 
	  public void init() { 
		  MockitoAnnotations.initMocks(this); 
	  }
	 
	
	@Test
	public void testFindAll() 
    {
        // give
        Product product1 = new Product("1", "alexa", "assitant device", "entertainment", "8000",10);
        Product product2 = new Product("2", "bluetooth speaker", "music device", "entertainment", "5000",5);
        List<Product> products = new ArrayList<Product>();
        products.add(product1);
        products.add(product2);
        String category="entertainment";
        when(catalogService.getInventoryProductByCategory(category)).thenReturn(products);
 
        
 
        // when
        List<Product> result = catalogController.getInventoryProductByCategory(category);
 
        // then
        assertThat(result.size()).isEqualTo(2);
         
        assertThat(result.get(0).getProductName())
                        .isEqualTo(product1.getProductName());
         
        assertThat(result.get(1).getProductName())
                       .isEqualTo(product2.getProductName());
    }

}
