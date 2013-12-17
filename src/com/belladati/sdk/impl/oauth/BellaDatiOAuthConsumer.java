package com.belladati.sdk.impl.oauth;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

import com.belladati.httpclientandroidlib.client.methods.HttpUriRequest;

public class BellaDatiOAuthConsumer extends AbstractOAuthConsumer {

	/** The serialVersionUID */
	private static final long serialVersionUID = 7676787639728090446L;

	public BellaDatiOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

	@Override
	protected HttpRequest wrap(Object request) {
		if (!(request instanceof com.belladati.httpclientandroidlib.HttpRequest)) {
			throw new IllegalArgumentException("This consumer expects requests of type "
				+ com.belladati.httpclientandroidlib.HttpRequest.class.getCanonicalName());
		}

		return new BellaDatiHttpRequestAdapter((HttpUriRequest) request);
	}
}
