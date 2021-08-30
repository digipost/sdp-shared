package no.digipost.api.xml;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;

import static co.unruly.matchers.Java8Matchers.where;
import static java.lang.reflect.Modifier.isStatic;
import static org.hamcrest.MatcherAssert.assertThat;

public class SchemasTest {

    @Test
    public void allSchemasExist() throws Exception {
        for (Field field : Schemas.class.getFields()) {
            if (isStatic(field.getModifiers()) && Resource.class.isAssignableFrom(field.getType())) {
                Resource resource = (Resource) field.get(Schemas.class);
                assertThat(resource + " must exist! (declared in " + Schemas.class.getName() + "." + field.getName() + ")", resource, where(Resource::exists));
            }
        }
    }

}
