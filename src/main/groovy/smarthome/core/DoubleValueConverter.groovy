package smarthome.core

import org.apache.commons.logging.LogFactory;
import grails.databinding.converters.ValueConverter;

class DoubleValueConverter implements ValueConverter {

	private static final log = LogFactory.getLog(this)
	
	@Override
	boolean canConvert(Object value) {
		return value instanceof String && value.toString().isDouble();
	}

	@Override
	Object convert(Object value) {
		// le canConvert protège des valeurs nulles et "non double" 
		return Double.valueOf(value.toString())
	}

	@Override
	Class<?> getTargetType() {
		return Double.class;
	}

}
