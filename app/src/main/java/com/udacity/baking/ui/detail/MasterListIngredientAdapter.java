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

package com.udacity.baking.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.baking.R;
import com.udacity.baking.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by McCrog on 12/05/2018.
 *
 */

class MasterListIngredientAdapter extends RecyclerView.Adapter<MasterListIngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> mIngredients = new ArrayList<>();

    public MasterListIngredientAdapter() {
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.mIngredient.setText(mIngredients.get(position).getIngredient());
        holder.mQuantity.setText(String.valueOf(mIngredients.get(position).getQuantity()));
        holder.mMeasure.setText(mIngredients.get(position).getMeasure());
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    public void setData(List<Ingredient> newIngredients) {
        mIngredients.clear();
        mIngredients.addAll(newIngredients);
        notifyDataSetChanged();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_card_view_ingredient_tv)
        TextView mIngredient;
        @BindView(R.id.ingredient_card_view_quantity_tv)
        TextView mQuantity;
        @BindView(R.id.ingredient_card_view_measure_tv)
        TextView mMeasure;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}