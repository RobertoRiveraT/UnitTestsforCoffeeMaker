/*
 * Copyright (c) 2009,  Sarah Heckman, Laurie Williams, Dright Ho
 * All Rights Reserved.
 * 
 * Permission has been explicitly granted to the University of Minnesota 
 * Software Engineering Center to use and distribute this source for 
 * educational purposes, including delivering online education through
 * Coursera or other entities.  
 * 
 * No warranty is given regarding this software, including warranties as
 * to the correctness or completeness of this software, including 
 * fitness for purpose.
 * 
 * Modifications
 * 20171113 - Michael W. Whalen - Extended with additional recipe.
 * 20171114 - Ian J. De Silva   - Updated to JUnit 4; fixed variable names.
 */
package edu.ncsu.csc326.coffeemaker;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;



/**
 * Unit tests for CoffeeMaker class.
 * 
 * @author Sarah Heckman
 *
 * Extended by Mike Whalen
 */
/**
 * Unit tests for CoffeeMaker with a stubbed RecipeBook using Mockito.
 * Only covers makeCoffee (Purchase Beverage) due to RecipeBook being incomplete.
 */
public class CoffeeMakerTestMock {
// public class CoffeeMakerTest {

    private CoffeeMaker coffeeMaker;
    private RecipeBook recipeBookStub;
    private Inventory inventory;

    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe[] recipes;

    @Before
    public void setUp() throws RecipeException {
        recipeBookStub = mock(RecipeBook.class);
        inventory = new Inventory();
        coffeeMaker = new CoffeeMaker(recipeBookStub, inventory);

        // Mocked recipe 1
        recipe1 = mock(Recipe.class);
        when(recipe1.getName()).thenReturn("Coffee");
        when(recipe1.getPrice()).thenReturn(50);
        when(recipe1.getAmtCoffee()).thenReturn(3);
        when(recipe1.getAmtMilk()).thenReturn(1);
        when(recipe1.getAmtSugar()).thenReturn(1);
        when(recipe1.getAmtChocolate()).thenReturn(0);

        // Mocked recipe 2 (not used but needed to test correct recipe is used)
        recipe2 = mock(Recipe.class);
        when(recipe2.getName()).thenReturn("Mocha");
        when(recipe2.getPrice()).thenReturn(75);
        when(recipe2.getAmtCoffee()).thenReturn(4);
        when(recipe2.getAmtMilk()).thenReturn(1);
        when(recipe2.getAmtSugar()).thenReturn(2);
        when(recipe2.getAmtChocolate()).thenReturn(2);

        recipes = new Recipe[]{recipe1, recipe2, null};
    }

    /**
     * ✅ Successful purchase, returns correct change.
     */
    @Test
    public void testSuccessfulPurchaseReturnsChange() {
        when(recipeBookStub.getRecipes()).thenReturn(recipes);
        int change = coffeeMaker.makeCoffee(0, 70);
        assertEquals(20, change);

        // Verifying that recipe1 methods were called
        verify(recipe1, times(1)).getAmtCoffee();
        verify(recipe1, times(1)).getAmtMilk();
        verify(recipe1, times(1)).getAmtSugar();
        verify(recipe1, times(1)).getAmtChocolate();
        verify(recipe1, times(1)).getPrice();

        // Verifying that recipe2 was not accessed
        verify(recipe2, never()).getAmtCoffee();
    }

    /**
     * ❌ E1: Not enough inventory - should return full money.
     */
    @Test
    public void testNotEnoughInventoryReturnsFull() {
        inventory.setCoffee(2); // recipe1 needs 3
        when(recipeBookStub.getRecipes()).thenReturn(recipes);

        int change = coffeeMaker.makeCoffee(0, 70);
        assertEquals(70, change); // full return
    }

    /**
     * ❌ E2: Not enough money - should return full money.
     */
    @Test
    public void testNotEnoughMoneyReturnsFull() {
        when(recipeBookStub.getRecipes()).thenReturn(recipes);
        int change = coffeeMaker.makeCoffee(0, 30); // needs 50
        assertEquals(30, change);
    }

    /**
     * ❌ E3: Invalid recipe index - should return full money.
     */
    @Test
    public void testInvalidIndexReturnsFullMoney() {
        when(recipeBookStub.getRecipes()).thenReturn(recipes);
        int change = coffeeMaker.makeCoffee(5, 100); // invalid index
        assertEquals(100, change);
    }
}
