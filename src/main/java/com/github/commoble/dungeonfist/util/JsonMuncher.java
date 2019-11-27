package com.github.commoble.dungeonfist.util;

import java.io.IOException;
import java.util.function.Consumer;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class JsonMuncher
{
	private JsonReader reader;
	
	public static JsonMuncher munch(JsonReader reader)
	{
		return new JsonMuncher(reader);
	}
	
	private JsonMuncher(JsonReader reader)
	{
		this.reader = reader;
	}
	
	/** entryConsumer.accept(reader) will run for each entry in the json object **/ 
	public void readObject(Consumer<JsonMuncher> entryConsumer)
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
	
	/** itemConsumer will run for for each json value in the json array **/
	public void readArray(Consumer<JsonMuncher> itemConsumer)
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
	
	/** Gets and consumes the next string value while inside a JSON object, throws IOException if next item is not a string **/
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

	
	private <T> T getValue(IOSupplier<T> jsonFunc)
	{
		try
		{
			return jsonFunc.get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
