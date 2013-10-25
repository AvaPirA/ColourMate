package com.avapir.colourmate.networking.comments;

import com.avapir.colourmate.networking.util.RequestCostructor;

public class CommentsRequestConstructor extends RequestCostructor {

	@Override
	protected String chooseMode(final Object... properties) {
		return null;
	}

	@Override
	protected String makeRequest(final String textRequest) {
		return super.makeRequest(textRequest);
	}

	@Override
	protected String otherProperties(final Object... properties) {
		return null;
	}

}
