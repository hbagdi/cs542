package studentOrientation.bookStore;

enum Books {
	HEAD_FIRST_DESIGN_PATTERNS("Head First Design Patterns",
			30.0), HEAD_FIRST_DESIGN_PATTERNS2("Head First Design Patterns2", 50.0);

	private final double price;
	private final String title;

	private Books(String title_in, double price_in) {
		title = title_in;
		price = price_in;
	}

	public double getPrice() {
		return price;
	}

	public String getTitle() {
		return title;
	}
}
