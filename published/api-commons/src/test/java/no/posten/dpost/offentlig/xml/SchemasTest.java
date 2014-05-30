package no.posten.dpost.offentlig.xml;

import org.junit.Test;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isStatic;
import static org.junit.Assert.assertTrue;

public class SchemasTest {

	@Test
    public void allSchemasExist() throws Exception {
		for (Field field : Schemas.class.getFields()) {
			if (isStatic(field.getModifiers()) && Resource.class.isAssignableFrom(field.getType())) {
				Resource resource = (Resource) field.get(Schemas.class);
				assertTrue(resource + " must exist! (declared in " + Schemas.class.getName() + "." + field.getName() + ")", resource.exists());
			}
		}
    }

}
