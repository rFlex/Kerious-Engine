package net.kerious.engine.utils;

import me.corsin.javatools.misc.PoolableImpl;
import net.kerious.engine.network.protocol.KeriousSerializableData;

public abstract class Controller<T extends KeriousSerializableData> extends PoolableImpl {

	////////////////////////
	// VARIABLES
	////////////////
	
	/**
	 * The controller model. Even though the field is accessible
	 * from inherited classes, it is only for the sake of the
	 * code performance. If you want to change the model, please
	 * use setModel()
	 */
	protected T model;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Controller() {
		super();
	}

	////////////////////////
	// METHODS
	////////////////

	abstract public void initialize();
	
	/**
	 * Called when the Controller's model has been changed
	 */
	abstract protected void modelChanged();
	
	@Override
	public void reset() {
		super.reset();
		
		this.setModel(null);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public T getModel() {
		return this.model;
	}

	public void setModel(T model) {
		if (this.model != model) {
			if (this.model != null) {
				this.model.release();
			}
			
			this.model = model;
			
			if (model != null) {
				model.retain();
			}
			
			this.modelChanged();
		}
	}

}
