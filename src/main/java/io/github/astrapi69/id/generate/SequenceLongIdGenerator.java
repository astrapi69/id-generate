/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.id.generate;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.prefs.Preferences;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import io.github.astrapi69.data.identifiable.IdGenerator;
import io.github.astrapi69.id.generate.enumtype.SequenceValue;

/**
 * The class {@link SequenceLongIdGenerator} is an implementation of {@link IdGenerator} interface
 * with id type of {@link Long} object. Additional the id generators can be saved on the local
 * preferences
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SequenceLongIdGenerator implements IdGenerator<Long>
{

	private static final Preferences PREFERENCES = Preferences
		.userNodeForPackage(SequenceLongIdGenerator.class);
	private static final AtomicLong ATOMIC_ID_COUNTER = new AtomicLong(Integer.parseInt(PREFERENCES
		.get(SequenceValue.DEFAULT_SEQUENCE_ID_KEY, SequenceValue.DEFAULT_SEQUENCE_INITIAL_VALUE)));
	private static final Map<Long, SoftReference<SequenceLongIdGenerator>> GENERATORS = new ConcurrentHashMap<>();
	private static final SequenceLongIdGenerator DEFAULT_ID_GENERATOR = new SequenceLongIdGenerator(
		0L, Long.parseLong(PREFERENCES.get(SequenceValue.DEFAULT_SEQUENCE_PREFIX + "0",
			SequenceValue.DEFAULT_SEQUENCE_INITIAL_VALUE)));

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			GENERATORS.values().stream().map(SoftReference::get)
				.filter(seq -> seq != null && seq.isPersistOnExit())
				.forEach(SequenceLongIdGenerator::persist);
			if (DEFAULT_ID_GENERATOR.isPersistOnExit())
			{
				DEFAULT_ID_GENERATOR.persist();
			}
			PREFERENCES.put(SequenceValue.DEFAULT_SEQUENCE_ID_KEY, ATOMIC_ID_COUNTER.toString());
		}));
	}

	@Getter
	long sequenceId;
	AtomicLong atomicIdCounter;
	AtomicBoolean persistOnExit;

	{
		persistOnExit = new AtomicBoolean(false);
	}

	private SequenceLongIdGenerator(long sequenceId, long initialValue)
	{
		this.sequenceId = sequenceId;
		atomicIdCounter = new AtomicLong(initialValue);
	}

	/**
	 * Gets the default {@link SequenceLongIdGenerator} object
	 *
	 * @return
	 */
	public static SequenceLongIdGenerator getDefaultIdGenerator()
	{
		return DEFAULT_ID_GENERATOR;
	}

	/**
	 * Gets the {@link SequenceLongIdGenerator} from the given sequenceId from the preference or
	 * create one if not
	 *
	 * @param sequenceId
	 *            the sequence id
	 * @return the {@link SequenceLongIdGenerator} object
	 */
	public static SequenceLongIdGenerator get(long sequenceId)
	{
		if (sequenceId < 0)
		{
			throw new IllegalArgumentException("(sequenceId = " + sequenceId + ") < 0");
		}
		if (sequenceId == 0)
		{
			return DEFAULT_ID_GENERATOR;
		}
		SoftReference<SequenceLongIdGenerator> r = GENERATORS.computeIfAbsent(sequenceId, sid -> {
			try
			{
				return new SoftReference<>(new SequenceLongIdGenerator(sid, Long.parseLong(
					PREFERENCES.get(SequenceValue.DEFAULT_SEQUENCE_PREFIX + sid, null))));
			}
			catch (Throwable t)
			{
				return null;
			}
		});
		return r == null ? null : r.get();
	}

	/**
	 * Factory method for create a new custom {@link SequenceLongIdGenerator} with an initial value
	 * of 1
	 */
	public static SequenceLongIdGenerator newSequenceIdGenerator()
	{
		return newSequenceIdGenerator(1);
	}

	/**
	 * Factory method for create a new custom {@link SequenceLongIdGenerator} with an initial value
	 *
	 * @param initialValue
	 *            the initial value for the generator
	 */
	public static SequenceLongIdGenerator newSequenceIdGenerator(long initialValue)
	{
		long sequenceId = ATOMIC_ID_COUNTER.getAndIncrement();
		SequenceLongIdGenerator sequenceIdGenerator = new SequenceLongIdGenerator(sequenceId,
			Long.parseLong(PREFERENCES.get(SequenceValue.DEFAULT_SEQUENCE_PREFIX + sequenceId,
				"" + initialValue)));
		GENERATORS.put(sequenceId, new SoftReference<>(sequenceIdGenerator));
		return sequenceIdGenerator;
	}

	/**
	 * Gets the current id value
	 *
	 * @return the current id value
	 */
	public Long getCurrentId()
	{
		return atomicIdCounter.get();
	}

	/**
	 * Gets the flag if this {@link SequenceLongIdGenerator} object will be persisted to the
	 * preferences
	 *
	 * @return true if this {@link SequenceLongIdGenerator} object will be persisted to the
	 *         preferences otherwise false
	 */
	public boolean isPersistOnExit()
	{
		return persistOnExit.get();
	}

	/**
	 * Sets the flag if this {@link SequenceLongIdGenerator} object will be persisted to the
	 * preferences
	 *
	 * @param persistOnExit
	 *            the flag if this {@link SequenceLongIdGenerator} object will be persisted to the
	 *            preferences
	 */
	public SequenceLongIdGenerator setPersistOnExit(boolean persistOnExit)
	{
		this.persistOnExit.set(persistOnExit);
		return this;
	}

	/**
	 * Persist the current {@link SequenceLongIdGenerator} object to the preferences
	 */
	public void persist()
	{
		PREFERENCES.put(SequenceValue.DEFAULT_SEQUENCE_PREFIX + sequenceId,
			String.valueOf(getCurrentId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		GENERATORS.remove(sequenceId);
		if (persistOnExit.get())
		{
			persist();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(sequenceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		return Objects.equals(this, obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "{" + "sequence=" + sequenceId + ", counter=" + atomicIdCounter + '}';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getNextId()
	{
		return atomicIdCounter.getAndIncrement();
	}

}
