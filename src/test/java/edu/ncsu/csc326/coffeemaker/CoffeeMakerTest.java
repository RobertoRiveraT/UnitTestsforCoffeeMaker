package edu.ncsu.csc326.coffeemaker;

import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoffeeMakerTest {

    private CoffeeMaker coffeeMaker;
    private Recipe r1, r2, r3;

    /**
     * Setup test environment before each test.
     * Creates 3 recipes and adds them to the CoffeeMaker.
     */
    @Before
    public void setUp() throws RecipeException {
        coffeeMaker = new CoffeeMaker();

        r1 = new Recipe();
        r1.setName("Coffee");
        r1.setAmtCoffee("3");
        r1.setAmtMilk("1");
        r1.setAmtSugar("1");
        r1.setAmtChocolate("0");
        r1.setPrice("50");

        r2 = new Recipe();
        r2.setName("Latte");
        r2.setAmtCoffee("2");
        r2.setAmtMilk("3");
        r2.setAmtSugar("2");
        r2.setAmtChocolate("0");
        r2.setPrice("75");

        r3 = new Recipe();
        r3.setName("Mocha");
        r3.setAmtCoffee("3");
        r3.setAmtMilk("1");
        r3.setAmtSugar("1");
        r3.setAmtChocolate("2");
        r3.setPrice("100");

        coffeeMaker.addRecipe(r1);
        coffeeMaker.addRecipe(r2);
        coffeeMaker.addRecipe(r3);
    }

    /**
     * Should NOT allow adding a recipe with the same name.
     * Expected result: addRecipe returns false.
     */
    @Test
    public void testAddRecipeDuplicateName() throws RecipeException {
        Recipe duplicate = new Recipe();
        duplicate.setName("Coffee"); // Same name as r1
        duplicate.setAmtCoffee("1");
        duplicate.setAmtMilk("1");
        duplicate.setAmtSugar("1");
        duplicate.setAmtChocolate("1");
        duplicate.setPrice("25");

        assertFalse("Should not add duplicate recipe name", coffeeMaker.addRecipe(duplicate));
    }

    /**
     * Should return the full amount if not enough money is provided.
     * Expected result: makeCoffee returns 25 as change.
     */
    @Test
    public void testMakeCoffeeNotEnoughMoney() {
        int change = coffeeMaker.makeCoffee(0, 25); // r1 costs 50
        assertEquals("Should return full change if not enough money", 25, change);
    }

    /**
     * Should reduce inventory after a successful purchase.
     * 
     * Expected:
     *  - Coffee reduced from 15 to 12 (used 3 for r1)
     *  - Milk reduced from 15 to 14 (used 1 for r1)
     *  - Sugar reduced from 15 to 14 (used 1 for r1)
     *  - Chocolate stays at 15 (used 0 for r1)
     */
    @Test
    public void testMakeCoffeeReducesInventory() {
        // Make coffee using recipe r1
        coffeeMaker.makeCoffee(0, 75); // r1 costs 50

        // Get inventory as a formatted string
        String inventory = coffeeMaker.checkInventory();
        System.out.println("DEBUG INVENTORY:\n" + inventory);

        // Check for expected inventory values
        assertTrue("Coffee should be reduced to 12", inventory.contains("Coffee: 12"));
        assertTrue("Milk should be reduced to 14", inventory.contains("Milk: 14"));
        assertTrue("Sugar should be reduced to 14", inventory.contains("Sugar: 14"));
        assertTrue("Chocolate should remain at 15", inventory.contains("Chocolate: 15"));
    }

    /**
     * Should delete the recipe at index 0.
     * Expected result: returns recipe name and recipe slot becomes null.
     */
    @Test
    public void testDeleteRecipe() {
        assertNotNull("Recipe should be deleted", coffeeMaker.deleteRecipe(0));
        assertNull("Recipe should be null after deletion", coffeeMaker.getRecipes()[0]);
    }

    /**
     * Should throw an exception if trying to add negative inventory.
     * Expected result: exception thrown.
     */
    @Test
    public void testAddInventoryNegativeShouldFail() {
        try {
            coffeeMaker.addInventory("-1", "1", "1", "1");
            fail("Adding negative inventory should throw exception");
        } catch (Exception e) {
            // Expected
        }
    }

    /**
     * Should correctly add inventory values.
     * Expected result: inventory is increased by 1 for each item.
     */
    @Test
    public void testAddInventoryNormal() {
        try {
            coffeeMaker.addInventory("1", "1", "1", "1");
            String inventory = coffeeMaker.checkInventory();
            assertTrue(inventory.contains("Coffee: 16")); // 15 base + 1
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testDeleteRecipeActuallyRemovesRecipe() {
        coffeeMaker.deleteRecipe(1); // Try to delete r2

        Recipe[] recipes = coffeeMaker.getRecipes();
        assertNull("Recipe should be null after deletion", recipes[1]);

        // Intentar hacer la receta eliminada debería fallar (retornar el mismo dinero)
        int change = coffeeMaker.makeCoffee(1, 100);
        assertEquals("Should return full amount if recipe is null", 100, change);
    }

    @Test
    public void testSetNegativePriceShouldThrowException() {
        Recipe invalid = new Recipe();
        try {
            invalid.setPrice("-10");
            fail("Setting negative price should throw RecipeException");
        } catch (RecipeException e) {
            // Expected
        }
    }

    /**
     * Should NOT allow adding more than 3 recipes.
     * Expected: addRecipe returns false cuando intentas añadir la 4ª receta.
     */
    @Test
    public void testAddRecipeBeyondLimit() throws RecipeException {
        Recipe r4 = new Recipe();
        r4.setName("Extra");
        r4.setAmtCoffee("1");
        r4.setAmtMilk("1");
        r4.setAmtSugar("1");
        r4.setAmtChocolate("1");
        r4.setPrice("10");

        // Ya hay 3 recetas (r1, r2, r3). La cuarta no debe aceptarse.
        assertFalse("Should not add a 4th recipe", coffeeMaker.addRecipe(r4));
    }

    /**
     * Should correctly edit an existing recipe by suplantar la lógica de precio
     * Expected:
     *  - editRecipe devuelve el nombre antiguo ("Latte")
     *  - tras editar, makeCoffee usa el nuevo precio (60): con 60 centavos debe devolver 0 de cambio.
     */
    @Test
    public void testEditRecipeAffectsPricing() throws RecipeException {
        // Nueva receta para sustituir a r2 (índice 1)
        Recipe newRec = new Recipe();
        newRec.setName("Changed");
        newRec.setAmtCoffee("1");
        newRec.setAmtMilk("2");
        newRec.setAmtSugar("3");
        newRec.setAmtChocolate("1");
        newRec.setPrice("60");

        // editRecipe debe devolver el nombre anterior
        String oldName = coffeeMaker.editRecipe(1, newRec);
        assertEquals("Should return old recipe name", "Latte", oldName);

        // Ahora r2 cuesta 60: paying 60 debe consumir y devolver 0 de cambio
        int change = coffeeMaker.makeCoffee(1, 60);
        assertEquals("After edit, recipe #1 should cost 60", 0, change);

        // Y si pagas menos que 60, deberías recibir todo de vuelta
        int change2 = coffeeMaker.makeCoffee(1, 50);
        assertEquals("With insufficient funds (50<60), full change returned", 50, change2);
    }

    /**
     * After editRecipe, getRecipes()[i] must return the NEW Recipe object,
     * no el ANTIGUO.
     */
    @Test
    public void testEditRecipeSlotContents() throws RecipeException {
        Recipe newRec = new Recipe();
        newRec.setName("Changed");
        newRec.setAmtCoffee("1");
        newRec.setAmtMilk("2");
        newRec.setAmtSugar("3");
        newRec.setAmtChocolate("1");
        newRec.setPrice("60");

        coffeeMaker.editRecipe(1, newRec);

        Recipe[] arr = coffeeMaker.getRecipes();
        // En la versión buggy sigue apareciendo "Latte" en arr[1]
        assertSame("Slot 1 must now reference the exact newRec instance",
                newRec, arr[1]);
    }

	/**
	 * Should return zero change when paying exact price.
	 * Expected:
	 *  - For r3 (Mocha) cost is 100, paying 100 debe devolver 0.
	 */
	@Test
	public void testMakeCoffeeExactPayment() throws RecipeException {
		int change = coffeeMaker.makeCoffee(2, 100);
		assertEquals("Exact payment should yield no change", 0, change);

		// Si pagas más, devolverá la diferencia:
		int change2 = coffeeMaker.makeCoffee(2, 110);
		assertEquals("Overpayment returns difference", 10, change2);
	}

}
