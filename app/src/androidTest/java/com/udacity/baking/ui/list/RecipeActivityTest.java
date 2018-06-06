/*
 * Copyright (C) 2018. The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking.ui.list;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.baking.R;
import com.udacity.baking.utilities.RecipeIdlingResource;
import com.udacity.baking.utilities.RecyclerViewMatcher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by McCrog on 06/06/2018.
 *
 */
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private static final String APP_NAME = "Baking";

    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String INGREDIENT_NAME = "unsalted butter";
    private static final String STEP_NAME = "Recipe Introduction";

    private static final String INGREDIENT_TEXT = "9 ingredients";
    private static final String STEP_TEXT = "7 steps";
    private static final String SERVINGS_TEXT = "8";

    private RecipeIdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(mIdlingResource);

    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    @Test
    public void checkActionBarTitle() {
        String supportActionBarTitle = String.valueOf(mActivityTestRule.getActivity().getSupportActionBar().getTitle());
        Assert.assertEquals(APP_NAME, supportActionBarTitle);
    }

    @Test
    public void checkGridViewItem_RecipeTitle() {
        onView(withRecyclerView(R.id.recipes_recycle_view)
                .atPositionOnView(0, R.id.recipe_card_view_name_tv))
                .check(matches(withText(RECIPE_NAME)));
    }

    @Test
    public void checkGridViewItem_IngredientText() {
        onView(withRecyclerView(R.id.recipes_recycle_view)
                .atPositionOnView(0, R.id.recipe_card_view_ingredients_tv))
                .check(matches(withText(INGREDIENT_TEXT)));
    }

    @Test
    public void checkGridViewItem_StepText() {
        onView(withRecyclerView(R.id.recipes_recycle_view)
                .atPositionOnView(0, R.id.recipe_card_view_steps_tv))
                .check(matches(withText(STEP_TEXT)));
    }

    @Test
    public void checkGridViewItem_ServingsText() {
        onView(withRecyclerView(R.id.recipes_recycle_view)
                .atPositionOnView(0, R.id.recipe_card_view_servings_tv))
                .check(matches(withText(SERVINGS_TEXT)));
    }

    @Test
    public void clickGridViewItem_OpensRecipeDetailActivity_CheckIngredientName() {
        onView(withId(R.id.recipes_recycle_view)).perform(RecyclerViewActions
                .actionOnItemAtPosition(1, click()));

        onView(withRecyclerView(R.id.ingredients_recycle_view)
                .atPositionOnView(1, R.id.ingredient_card_view_ingredient_tv))
                .check(matches(withText(INGREDIENT_NAME)));
    }

    @Test
    public void clickGridViewItem_OpensRecipeDetailActivity_CheckStepName() {
        onView(withId(R.id.recipes_recycle_view)).perform(RecyclerViewActions
                .actionOnItemAtPosition(1, click()));

        onView(withRecyclerView(R.id.steps_recycle_view)
                .atPositionOnView(0, R.id.step_card_view_short_description_tv))
                .check(matches(withText(STEP_NAME)));
    }
}