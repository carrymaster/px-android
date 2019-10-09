package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import java.util.ArrayList;
import java.util.List;

public class SummaryView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    @NonNull private final ElementDescriptorView bigHeaderDescriptor;
    @NonNull private final AmountDescriptorView totalAmountDescriptor;
    @NonNull private final FrameLayout itemsContainer;
    private final DetailAdapter detailAdapter;

    private final RecyclerView detailRecyclerView;
    private OnFitListener listener;
    @Nullable private OnMeasureListener measureListener;

    private final Animation listAppearAnimation;
    private final Animation logoAppearAnimation;
    private final Animation logoDisappearAnimation;

    private boolean showingBigLogo = false;
    private boolean animating = false;
    private int maxElementsToShow;

    public SummaryView(final Context context) {
        this(context, null);
    }

    public SummaryView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SummaryView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.px_view_express_summary, this);
        itemsContainer = findViewById(R.id.items_container);
        bigHeaderDescriptor = findViewById(R.id.bigElementDescriptor);
        bigHeaderDescriptor.setVisibility(INVISIBLE);
        totalAmountDescriptor = findViewById(R.id.total);
        detailRecyclerView = findViewById(R.id.recycler);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        detailAdapter = new DetailAdapter();
        detailRecyclerView.setAdapter(detailAdapter);
        listAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_list_appear);
        listAppearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
                detailRecyclerView.setAlpha(1.0f);
                animating = true;
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        logoAppearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_logo_appear);
        logoDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.px_summary_logo_disappear);
    }

    public void setMaxElementsToShow(final int maxElementsToShow) {
        this.maxElementsToShow = maxElementsToShow;
    }

    public void setBigHeaderListener(@NonNull final OnClickListener listener) {
        bigHeaderDescriptor.setOnClickListener(listener);
    }

    public void setOnFitListener(final OnFitListener listener) {
        this.listener = listener;
    }

    public void setMeasureListener(@Nullable final OnMeasureListener measureListener) {
        this.measureListener = measureListener;
    }

    private boolean isViewOverlapping(final View firstView, final View secondView) {
        final int yFirstViewEnd = firstView.getTop() + firstView.getHeight();
        final int ySecondViewInit = secondView.getTop();

        return yFirstViewEnd >= ySecondViewInit;
    }

    public void animateElementList(final float positionOffset) {
        if (!animating) {
            detailRecyclerView.setAlpha(1.0f - positionOffset);
        }
    }

    public void update(@NonNull final Model model) {
        if (model.headerDescriptor != null) {
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            bigHeaderDescriptor.update(model.headerDescriptor);
        } else {
            bigHeaderDescriptor.setVisibility(GONE);
        }

        totalAmountDescriptor.setTextSize(R.dimen.px_l_text);
        totalAmountDescriptor.setBold(AmountDescriptorView.Position.RIGHT);
        totalAmountDescriptor.update(model.total);

        detailAdapter.updateItems(model.elements);
        detailRecyclerView.startAnimation(listAppearAnimation);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
        super.onLayout(changed, l, t, r, b);
        if (measureListener != null) {
            final int availableSummaryHeight = itemsContainer.getMeasuredHeight();
            final float singleItemHeight = AmountDescriptorView.getDesiredHeight(getContext());
            final int expectedItemsHeight = Math.round(singleItemHeight * maxElementsToShow);
            measureListener.onSummaryMeasured(expectedItemsHeight > availableSummaryHeight);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (isViewOverlapping(bigHeaderDescriptor, detailRecyclerView)) {
            if (showingBigLogo) {
                showingBigLogo = false;
                bigHeaderDescriptor.startAnimation(logoDisappearAnimation);
                if (listener != null) {
                    listener.onBigHeaderOverlaps();
                }
            }
        } else if (!showingBigLogo) {
            bigHeaderDescriptor.setVisibility(VISIBLE);
            showingBigLogo = true;
            bigHeaderDescriptor.startAnimation(logoAppearAnimation);

            if (listener != null) {
                listener.onBigHeaderDoesNotOverlaps();
            }
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public interface OnFitListener {
        void onBigHeaderOverlaps();
        void onBigHeaderDoesNotOverlaps();
    }

    public interface OnMeasureListener {
        void onSummaryMeasured(boolean itemsClipped);
    }

    public static final class Model {

        /* default */ @NonNull final List<AmountDescriptorView.Model> elements;

        /* default */ @Nullable final ElementDescriptorView.Model headerDescriptor;

        /* default */ @NonNull final AmountDescriptorView.Model total;

        public Model(@Nullable final ElementDescriptorView.Model headerDescriptor,
            @NonNull final List<AmountDescriptorView.Model> elements,
            @NonNull final AmountDescriptorView.Model total) {
            this.elements = elements;
            this.headerDescriptor = headerDescriptor;
            this.total = total;
        }

        public int getElementsSize() {
            return elements.size();
        }
    }

    /* default */ static final class DetailAdapter extends RecyclerView.Adapter<AmountViewHolder> {
        @NonNull private List<AmountDescriptorView.Model> items;

        /* default */ DetailAdapter() {
            items = new ArrayList<>();
        }

        @Override
        public AmountViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view =
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.px_viewholder_amountdescription, parent, false);
            return new AmountViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AmountViewHolder holder, final int position) {
            holder.populate(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void updateItems(@NonNull final List<AmountDescriptorView.Model> items) {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    /* default */ static class AmountViewHolder extends RecyclerView.ViewHolder {

        private final AmountDescriptorView amountDescView;

        public AmountViewHolder(final View itemView) {
            super(itemView);
            amountDescView = (AmountDescriptorView) itemView;
        }

        public void populate(@NonNull final AmountDescriptorView.Model model) {
            amountDescView.update(model);
        }
    }
}