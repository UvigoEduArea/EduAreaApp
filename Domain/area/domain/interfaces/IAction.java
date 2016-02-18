package area.domain.interfaces;

/**
 * 
 * @author luis
 *
 * @param <T>
 */
public interface IAction<T> {
	
	void Do(T param);
}
