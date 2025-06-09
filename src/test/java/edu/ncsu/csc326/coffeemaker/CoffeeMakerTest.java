package edu.ncsu.csc326.coffeemaker;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoffeeMakerTest {

    //Inventory unit tests
    private Inventory inventory; 
    //Recipie unit tests
    private Recipe recipe; 
    private RecipeBook book;
    private CoffeeMaker coffeeMaker;
    private Recipe r1, r2, r3;

    /**
     * Setup test environment before each test.
     * Creates 3 recipes and adds them to the CoffeeMaker.
     */
    @Before
    public void setUp() throws RecipeException {
        book = new RecipeBook();
        inventory = new Inventory();
        recipe = new Recipe();
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
        String _inventory = coffeeMaker.checkInventory();
        System.out.println("DEBUG INVENTORY:\n" + inventory);

        // Check for expected inventory values
        assertTrue("Coffee should be reduced to 12", _inventory.contains("Coffee: 12"));
        assertTrue("Milk should be reduced to 14", _inventory.contains("Milk: 14"));
        assertTrue("Sugar should be reduced to 14", _inventory.contains("Sugar: 14"));
        assertTrue("Chocolate should remain at 15", _inventory.contains("Chocolate: 15"));
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
            String _inventory = coffeeMaker.checkInventory();
            assertTrue(_inventory.contains("Coffee: 16")); // 15 base + 1
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
// ========================
// Coffe Maker unit tests
// ========================

    @Test
    public void testAddInventoryValidInputs() throws InventoryException {
        CoffeeMaker cm = new CoffeeMaker();
        cm.addInventory("3", "2", "1", "4"); // All valid
        String _inventory = cm.checkInventory();
        assertTrue(_inventory.contains("Coffee: 18"));
        assertTrue(_inventory.contains("Milk: 17"));
        assertTrue(_inventory.contains("Sugar: 16"));
        assertTrue(_inventory.contains("Chocolate: 19"));
    }

    @Test(expected = InventoryException.class)
    public void testAddInventoryInvalidNumberFormat() throws InventoryException {
        CoffeeMaker cm = new CoffeeMaker();
        cm.addInventory("two", "1", "1", "1"); // coffee is invalid
    }

    @Test(expected = InventoryException.class)
    public void testAddInventoryNegativeValue() throws InventoryException {
        CoffeeMaker cm = new CoffeeMaker();
        cm.addInventory("-1", "0", "0", "0");
    }

    @Test
    public void testMakeCoffeeSuccessAndInventoryReduction() throws Exception {
        CoffeeMaker cm = new CoffeeMaker();
        Recipe r = new Recipe();
        r.setName("Test");
        r.setAmtCoffee("1");
        r.setAmtMilk("1");
        r.setAmtSugar("1");
        r.setAmtChocolate("1");
        r.setPrice("10");
        cm.addRecipe(r);

        int change = cm.makeCoffee(0, 15);
        assertEquals("Expected change after purchase", 5, change);

        String _inventory = cm.checkInventory();
        assertTrue(_inventory.contains("Coffee: 14"));
        assertTrue(_inventory.contains("Milk: 14"));
        assertTrue(_inventory.contains("Sugar: 14"));
        assertTrue(_inventory.contains("Chocolate: 14"));
    }

    @Test
    public void testMakeCoffeeNotEnoughMoney2() throws Exception {
        CoffeeMaker cm = new CoffeeMaker();
        Recipe r = new Recipe();
        r.setName("Cheap");
        r.setAmtCoffee("1");
        r.setAmtMilk("1");
        r.setAmtSugar("1");
        r.setAmtChocolate("1");
        r.setPrice("10");
        cm.addRecipe(r);

        int change = cm.makeCoffee(0, 5);
        assertEquals("Should return full amount if not enough money", 5, change);
    }

    @Test
    public void testMakeCoffeeInvalidRecipeIndex() throws Exception {
        CoffeeMaker cm = new CoffeeMaker();
        int change = cm.makeCoffee(2, 50); // No recipes added
        assertEquals("Should return full amount when no recipe at index", 50, change);
    }

    @Test
    public void testCheckInventoryDefaultValues() {
        CoffeeMaker cm = new CoffeeMaker();
        String _inventory = cm.checkInventory();
        assertTrue(_inventory.contains("Coffee: 15"));
        assertTrue(_inventory.contains("Milk: 15"));
        assertTrue(_inventory.contains("Sugar: 15"));
        assertTrue(_inventory.contains("Chocolate: 15"));
    }



// ========================
// Inventory unit tests
// ========================
    /**
     * Initial inventory should be 15 for all items.
     */
    @Test
    public void testInitialInventory() {
        assertEquals("Initial coffee should be 15", 15, inventory.getCoffee());
        assertEquals("Initial milk should be 15", 15, inventory.getMilk());
        assertEquals("Initial sugar should be 15", 15, inventory.getSugar());
        assertEquals("Initial chocolate should be 15", 15, inventory.getChocolate());
    }

    /**
     * Adding negative coffee should throw exception.
     * Expected: InventoryException is thrown.
     */
    /*
    @Test(expected = InventoryException.class)
    public void testAddNegativeCoffee() throws InventoryException {
        inventory.addCoffee(-5);
    }
    */

    /**
     * Should successfully consume ingredients when the recipe requirements are met.
     * Expected: useIngredients(recipe) returns true.
     */
    @Test
    public void testUseIngredientsSuccess() throws RecipeException {
        // construye una receta compatible con los valores por defecto (15,15,15,15)
        Recipe r = new Recipe();
        r.setAmtCoffee("3");
        r.setAmtMilk("2");
        r.setAmtSugar("1");
        r.setAmtChocolate("0");

        assertTrue("Should be able to use ingredients for a valid Recipe", inventory.useIngredients(r));
    }

    /**
     * Should NOT consume ingredients when the recipe requires more than available.
     * Expected: useIngredients(recipe) returns false.
     */
    @Test
    public void testUseIngredientsFailWhenInsufficient() throws RecipeException {
        // receta que pide más azúcar de la disponible
        Recipe r = new Recipe();
        r.setAmtCoffee("1");
        r.setAmtMilk("1");
        r.setAmtSugar("20");   // 20 > 15 default
        r.setAmtChocolate("0");

        assertFalse("Should not use ingredients when sugar is insufficient", inventory.useIngredients(r));
    }

    /**
     * toString should list all inventory items in readable format.
     * Expected: formatted string contains each item and count.
     */
    @Test
    public void testToStringFormat() {
        String s = inventory.toString();
        assertTrue(s.contains("Coffee: 15"));
        assertTrue(s.contains("Milk: 15"));
        assertTrue(s.contains("Sugar: 15"));
        assertTrue(s.contains("Chocolate: 15"));
    }

// ========================
// Recipe unit tests
// ========================

    /**
     * Should correctly set chocolate amount when given a valid positive integer string.
     * Expected: amtChocolate is set to 5 with no exception.
     */
    @Test
    public void testSetAmtChocolate_ValidInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtChocolate("5");
        assertEquals("Chocolate amount should be set to 5", 5, _recipe.getAmtChocolate());
    }

    /**
     * Should throw RecipeException when given a negative number.
     * Expected: RecipeException thrown with correct message.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtChocolate_NegativeInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtChocolate("-2");
    }

    /**
     * Should throw RecipeException when input is non-numeric.
     * Expected: RecipeException thrown with correct message.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtChocolate_NonNumericInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtChocolate("abc");
    }

    /**
     * Should correctly set milk amount when given a valid positive integer string.
     * Expected: amtMilk is set to 3 with no exception.
     */
    @Test
    public void testSetAmtMilk_ValidInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtMilk("3");
        assertEquals("Milk amount should be set to 3", 3, _recipe.getAmtMilk());
    }

    /**
     * Should throw RecipeException when given a negative number.
     * Expected: RecipeException thrown with correct message.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtMilk_NegativeInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtMilk("-1");
    }

    /**
     * Should throw RecipeException when input is non-numeric.
     * Expected: RecipeException thrown with correct message.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtMilk_NonNumericInput() throws RecipeException {
        Recipe _recipe = new Recipe();
        _recipe.setAmtMilk("leche");
    }

    /**
     * Setting coffee amount to non-numeric should throw RecipeException.
     * Expected: RecipeException is thrown.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtCoffeeNonNumeric() throws RecipeException {
        recipe.setAmtCoffee("abc");
    }

    /**
     * Setting coffee amount to negative value should throw RecipeException.
     * Expected: RecipeException is thrown.
     */
    @Test(expected = RecipeException.class)
    public void testSetAmtCoffeeNegative() throws RecipeException {
        recipe.setAmtCoffee("-1");
    }

    /**
     * getAmtCoffee should return the integer value previously set.
     * Expected: returns 4 when set to "4".
     */
    @Test
    public void testGetAmtCoffee() throws RecipeException {
        recipe.setAmtCoffee("4");
        assertEquals("Coffee amount should be 4", 4, recipe.getAmtCoffee());
    }

    /**
     * Two recipes with the same name should be considered equal.
     * Expected: equals() returns true.
     */
    @Test
    public void testEqualsSameName() throws RecipeException {
        recipe.setName("Same");
        Recipe other = new Recipe();
        other.setName("Same");
        assertTrue("Recipes with same name should be equal", recipe.equals(other));
    }

    /**
     * Two recipes with different names should not be equal.
     * Expected: equals() returns false.
     */
    @Test
    public void testEqualsDifferentName() throws RecipeException {
        recipe.setName("First");
        Recipe other = new Recipe();
        other.setName("Second");
        assertFalse("Recipes with different names should not be equal", recipe.equals(other));
    }

// ========================
// RecipeBook unit tests
// ========================
    /**
     * Should add and then delete a recipe correctly.
     * Expected: deleteRecipe returns the recipe name and the slot is null.
     */
    @Test
    public void testAddAndDeleteRecipe() throws RecipeException {
        RecipeBook book = new RecipeBook();
        Recipe r1 = new Recipe();
        r1.setName("A"); r1.setPrice("10");

        assertTrue(book.addRecipe(r1));
        String name = book.deleteRecipe(0);
        assertEquals("Deleted recipe name should match", "A", name);
        assertNull("Slot should be null after deletion", book.getRecipes()[0]);
    }

    /**
     * Should not allow adding beyond capacity.
     * Expected: adding the 4th recipe returns false.
     */
    @Test
    public void testAddBeyondCapacity() throws RecipeException {
        assertTrue(book.addRecipe(r1));
        assertTrue(book.addRecipe(r2));
        assertTrue(book.addRecipe(r3));

        Recipe r4 = new Recipe();
        r4.setName("Extra");
        r4.setAmtCoffee("1");
        r4.setAmtMilk("1");
        r4.setAmtSugar("1");
        r4.setAmtChocolate("1");
        r4.setPrice("10");
        assertFalse("Should not add 4th recipe", book.addRecipe(r4));
    }

    /**
     * Deleting with invalid index should return null and not throw.
     * Expected: deleteRecipe(-1) and deleteRecipe(3) return null.
     */
    @Test
    public void testDeleteInvalidIndex() {
        assertNull("Deleting index -1 should return null", book.deleteRecipe(-1));
        assertNull("Deleting index 3 should return null", book.deleteRecipe(3));
    }

    /**
     * Editing with invalid index should return null without exception.
     * Expected: editRecipe(5, r1) returns null.
     */
    @Test
    public void testEditInvalidIndex() throws RecipeException {
        assertNull("Editing index 5 should return null", book.editRecipe(5, r1));
    }

    /**
     * getRecipes should return array of correct initial length with null slots.
     * Expected: length == 3 and all entries null.
     */
    @Test
    public void testGetRecipesInitialLength() {
        Recipe[] arr = book.getRecipes();
        assertEquals("Initial recipes length should be 3", 3, arr.length);
        for (Recipe r : arr) {
            assertNull(r);
        }
    }

}
