/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
