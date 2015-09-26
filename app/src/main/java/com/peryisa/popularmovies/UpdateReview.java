package com.peryisa.popularmovies;

import java.util.ArrayList;

import model.Review;

/**
 * If one movie is chosen, then the reviews should be updated.
 * @author igiagante, on 17/9/15.
 */
public interface UpdateReview {
    void updateReviews(ArrayList<Review> reviews);
}
