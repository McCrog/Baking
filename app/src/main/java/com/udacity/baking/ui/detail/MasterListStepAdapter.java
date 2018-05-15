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

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.baking.R;
import com.udacity.baking.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alex on 12/05/2018.
 */

class MasterListStepAdapter extends RecyclerView.Adapter<MasterListStepAdapter.StepViewHolder>{

    private final List<Step> mSteps = new ArrayList<>();
    private final StepOnClickHandler mClickHandler;

    /**
     * The interface that receives onStepClick messages.
     */
    public interface StepOnClickHandler {
        void onStepClick(int position);
    }

    public MasterListStepAdapter(StepOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.mDescription.setText(mSteps.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public void setData(List<Step> newSteps) {
        mSteps.clear();
        mSteps.addAll(newSteps);
        notifyDataSetChanged();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_card_view)
        CardView mStepCardView;
        @BindView(R.id.step_card_view_short_description_tv)
        TextView mDescription;

        public StepViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onStepClick(position);
        }
    }
}
