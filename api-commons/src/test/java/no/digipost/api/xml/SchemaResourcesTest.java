package no.digipost.api.xml;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;

import static co.unruly.matchers.Java8Matchers.where;
import static java.lang.reflect.Modifier.isStatic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

class SchemaResourcesTest {

    @Test
    void allSchemasExist() throws Exception {
        for (Field field : SchemaResources.class.getFields()) {
            if (isStatic(field.getModifiers()) && Resource.class.isAssignableFrom(field.getType())) {
                SchemaResource resource = (SchemaResource) field.get(SchemaResources.class);
                assertThat(resource + " must exist! (declared in " + SchemaResources.class.getName() + "." + field.getName() + ")",
                        resource, where(SchemaResource::sizeInBytes, greaterThan(42)));
            }
        }
    }

}
