package wordCount.store;

import wordCount.visitors.Visitor;

/**
 * @author Hardik Bagdi (hbagdi1@binghamton.edu)
 *
 */
public interface Tree {

	// tree api
	public void insert(Word word);

	public void insert(Node node);

	public void remove(Word word);

	public void remove(Node node);

	public boolean contains(Word word);

	public boolean contains(Node toCheck);

	public Node getNode(Word data);

	// force accept() implementation
	public void accept(Visitor visitor);
}
