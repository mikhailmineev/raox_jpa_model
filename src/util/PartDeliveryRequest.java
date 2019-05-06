package util;

import domain.Part;

public class PartDeliveryRequest {

	public PartDeliveryRequest(Part part, int count) {
		this.part = part;
		this.count = count;
	}

	public PartDeliveryRequest(PartDeliveryRequest original) {
		this.part = original.part;
		this.count = original.count;
	}

	public Part part;
	public int count;
}

