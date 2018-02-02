package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;
import fr.inria.spirals.npefix.resi.context.Lapse;

import java.io.Serializable;
import java.util.Date;

public class RegressionRequest implements Serializable {
	private final Date date;
	private ShadowRequest request;
	private ShadowResponse response;
	private boolean validity;
	private Lapse lapse;

	public RegressionRequest() {
		this.date = new Date();
	}

	public void setRequest(ShadowRequest request) {
		this.request = request;
	}

	public ShadowRequest getRequest() {
		return request;
	}

	public void setResponse(ShadowResponse response) {
		this.response = response;
	}

	public ShadowResponse getResponse() {
		return response;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	public boolean isValidity() {
		return validity;
	}

	public void setLapse(Lapse lapse) {
		this.lapse = lapse;
	}

	public Lapse getLapse() {
		return lapse;
	}

	public Date getDate() {
		return date;
	}
}

