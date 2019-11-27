/**
The MIT License (MIT)

Copyright (c) 2019 Joseph Bettendorff a.k.a. "Commoble"

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

/**
 * Usage example:
 * 
 * Given the following json file:
 * 

{
	"dungeonfist:chestplates": 
	[
		"cloister",
		"contairer",
		"defender"
	],
	"dungeonfist:legs_and_feet": 
	[
		"dancer",
		"runner",
		"walker"
	],
	"dungeonfist:weapons": 
	[
		"blaster",
		"ender",
		"stabber"
	]
}

 * we can use JsonMuncher to convert this json file into a map of lists of strings:
 * 	
	public Map<ResourceLocation, List<String> readJson()
	{
		InputStream input = DungeonFist.class.getClassLoader.getResourceAsStream("data/dungeonfist/epic_names/suffixes.json");
		JsonReader reader = loadReader(input);
		return readJson(reader);
	}

 	private static JsonReader loadReader(InputStream input)
	{
		JsonReader reader = null;
		try
		{
			reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return reader;
	}
	
	private static Map<ResourceLocation, List<String>> readJson(JsonReader reader)
	{
		JsonReader reader = 
		Map<ResourceLocation, List<String>> map = new HashMap<>();
		JsonMuncher.munch(reader).readObject(mapMuncher -> {
			String key = mapMuncher.readKey();
			List<String> list = new ArrayList<String>();
			mapMuncher.readArray(arrayMuncher -> list.add(arrayMuncher.readString()));
			map.put(new ResourceLocation(key), list);
		});
		return map;
	}
	
 * or, that last function but more succinctly:

	private static Map<ResourceLocation, List<String>> readJson(JsonReader reader)
	{
		return JsonMuncher.munch(reader).readSimpleObject(
			ResourceLocation::new,
			mapMuncher -> mapMuncher.readSimpleArray(JsonMuncher::readString));
	}
 */

package com.github.commoble.dungeonfist.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Functions;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class JsonMuncher
{
	private final JsonReader reader;
	
	public static JsonMuncher munch(final JsonReader reader)
	{
		return new JsonMuncher(reader);
	}
	
	private JsonMuncher(final JsonReader reader)
	{
		this.reader = reader;
	}
	
	/** entryConsumer will run consume each entry in the json object **/ 
	public void readObject(final Consumer<JsonMuncher> entryConsumer)
	{
		try
		{
			this.reader.beginObject();
			
			while(this.reader.hasNext())
			{
				entryConsumer.accept(this);
			}
			
			this.reader.endObject();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/** Reads and consumes an object where each value in the object is of the same type, and generates a hashmap from it **/
	public <K,V> Map<K, V> readSimpleObject(final Function<String, K> keyFunction, final Function<JsonMuncher, V> valueFunction)
	{
		final Map<K, V> map = new HashMap<>();
		try
		{
			this.reader.beginObject();
			
			while(this.reader.hasNext())
			{
				map.put(keyFunction.apply(this.readKey()), valueFunction.apply(this));
			}
			
			this.reader.endObject();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/** Reads and consumes an object where each value in the object is of the same type, and generates a hashmap from it **/
	public <T> Map<String, T> readSimpleObject(final Function<JsonMuncher, T> function)
	{
		return this.readSimpleObject(Functions.identity(), function);
	}
	
	/** itemConsumer will consume each json value in the json array **/
	public void readArray(final Consumer<JsonMuncher> itemConsumer)
	{
		try
		{
			this.reader.beginArray();
			
			while(this.reader.hasNext())
			{
				itemConsumer.accept(this);
			}
			
			this.reader.endArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/** Reads and consumes a json array where each value in the array is of the same type **/
	public <T> List<T> readSimpleArray(final Function<JsonMuncher, T> function)
	{
		final List<T> list = new ArrayList<>();
		try
		{
			this.reader.beginArray();
			
			while(this.reader.hasNext())
			{
				list.add(function.apply(this));
			}
			
			this.reader.endArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/** Gets the type of the next json token without consuming it, throws IOException if json is malformed **/
	public JsonToken peek()
	{
		try
		{
			return this.reader.peek();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/** Consumes the next element without returning it, consumes entire arrays and objects, throws IOException if json is malformed **/
	public void skip()
	{
		try
		{
			this.reader.skipValue();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/** Gets and consumes the next key name while inside a JSON object, throws IOException if next item is not a key name **/
	public String readKey()
	{
		return this.getValue(this.reader::nextName);
	}
	
	/** Gets and consumes the next string value while inside a JSON object, throws IOException if next item is not a string value **/
	public String readString()
	{
		return this.getValue(this.reader::nextString);
	}
	
	/** Gets and consumes the next integer while inside a JSON object, throws IOException if next item is not an int **/
	public int readInt()
	{
		return this.getValue(this.reader::nextInt);
	}
	
	/** Gets and consumes the next long while inside a JSON object, throws IOException if next item is not a long **/
	public long readLong()
	{
		return this.getValue(this.reader::nextLong);
	}
	
	/** Gets and consumes the next double while inside a JSON object, throws IOException if next item is not a double **/
	public double readDouble()
	{
		return this.getValue(this.reader::nextDouble);
	}
	
	/** Gets and consumes the next boolean while inside a JSON object, throws IOException if next item is not a boolean **/
	public boolean readBoolean()
	{
		return this.getValue(this.reader::nextBoolean);
	}
	
	/** Consumes the next null value while inside a JSON object, throws IOException if next item is not a null **/
	public void readNull()
	{
		try
		{
			this.reader.nextNull();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@FunctionalInterface
	private interface IOSupplier<T>
	{
		T get() throws IOException;
	}

	
	private <T> T getValue(final IOSupplier<T> getter)
	{
		try
		{
			return getter.get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
