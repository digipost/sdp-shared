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

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Ignore
public class UnmarshallingTest {

	@Test
	public void test() {
		InputStream xml = getClass().getResourceAsStream("/difi2.xml");
		Marshalling.createUnManaged().unmarshal(new StreamSource(xml));
	}


}
