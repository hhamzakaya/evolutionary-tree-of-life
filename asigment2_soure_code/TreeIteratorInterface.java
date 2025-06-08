import java.util.Iterator;

public interface TreeIteratorInterface<T> {
   public Iterator<TreeNode<T>> getPreorderIterator();



   /**   ileride kullanmak içi dinamik bir yapi sağlar  .
   // public Iterator<T> getPostorderIterator();

   //public Iterator<T> getLevelOrderIterator();*/
}
