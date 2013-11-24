package net.kerious.engine.utils;

import me.corsin.javatools.misc.Pool;
import net.kerious.engine.KeriousException;
import net.kerious.engine.network.protocol.KeriousSerializableData;

/**
 * A controller factory offers an easy way to create controllers and their models
 * All methods are protected as they are meant to be encapsulated into more
 * self explanatory methods by the inheritance
 * @author corsin_s
 *
 * @param <ControllerType>
 * @param <ModelType>
 */
public abstract class ControllerFactory<ControllerType extends Controller<ModelType>, ModelType extends KeriousSerializableData> {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Pool<ControllerType> controllersPool;
	final private Pool<ModelType> modelsPool;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ControllerFactory() {
		super();
		
		this.controllersPool = new Pool<ControllerType>() {
			protected ControllerType instantiate() {
				return newController();
			}
		};
		this.modelsPool = new Pool<ModelType>() {
			protected ModelType instantiate() {
				return newModel();
			}
		};
	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected ControllerType newController();
	abstract protected ModelType newModel();
	
	/**
	 * Create a model
	 * @return
	 */
	protected ModelType createModel() {
		ModelType model = this.modelsPool.obtain();
		
		if (model == null) {
			throw new KeriousException("The model pool did not create a controller");
		}
		
		return model;
	}
	
	/**
	 * Create a controller and set a newly created model
	 * @return
	 */
	protected ControllerType createController() {
		ModelType model = this.createModel();
		
		// Retain 1
		
		ControllerType controller = this.createController(model);
		
		// Retain 2
		
		model.release();
		
		// Retain 1
		
		return controller;
	}
	
	/**
	 * Create a controller and set the model
	 * @param model
	 * @return
	 */
	protected ControllerType createController(ModelType model) {
		ControllerType controller = this.controllersPool.obtain();
		
		if (controller == null) {
			throw new KeriousException("The controller pool did not create a controller");
		}
		
		controller.setModel(model);
		
		controller.initialize();
		
		return controller;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
