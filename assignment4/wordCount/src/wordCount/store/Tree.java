package wordCount.store;

import wordCount.visitors.DSProcessingVisitorI;

/**
 * @author Hardik Bagdi (hbagdi1@binghamton.edu)
 *
 */
public interface Tree {

	// tree api
	public Node getRoot();

	public void setRoot(Node node);

	public void insert(Word word);;

	public void remove(Word word);

	public boolean contains(Word word);

	public boolean contains(Node toCheck);

	public Node getNode(Word data);

	// force accept() implementation
	public void accept(DSProcessingVisitorI visitor);

	// clone force
	public Object clone() throws CloneNotSupportedException;
}
