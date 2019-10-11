package org.jumahuaca.examples.jackson;

import static org.jumahuaca.assertions.AssertJackson.assertDeserialization;
import static org.jumahuaca.assertions.AssertJackson.assertListDeserialization;
import static org.jumahuaca.assertions.AssertJackson.assertSerialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jumahuaca.examples.model.UVAExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

public class JacksonJsonSerializationTests {

	private static final int EXCHANGE_1_YEAR = 2017;

	private static final int EXCHANGE_1_MONTH = 9;

	private static final int EXCHANGE_1_DAY = 20;

	private static final BigDecimal EXCHANGE_1_RATE = BigDecimal.valueOf(20.06);

	private static final int EXCHANGE_2_YEAR = 2017;

	private static final int EXCHANGE_2_MONTH = 11;

	private static final int EXCHANGE_2_DAY = 10;

	private static final BigDecimal EXCHANGE_2_RATE = BigDecimal.valueOf(20.62);

	private static final String EXCHANGE_1_SERIALIZED_STRING = "{\"date\":[2017,9,20],\"rate\":20.06}";

	private static final String EXCHANGE_SERIALIZED_LIST_STRING = "[{\"date\":[2017,9,20],\"rate\":20.06},{\"date\":[2017,11,10],\"rate\":20.62}]";

	private static final String EXCHANGE_SERIALIZED_EMPTY_LIST_STRING = "[]";

	private static final String EXCHANGE_NULL_SERIALIZED = "{\"date\":null,\"rate\":null}";

	private static final String EXCHANGE_LIST_NULL_SERIALIZED = "null";

	private static ObjectMapper mapper;

	private static UVAExchange exchangeToTest1;

	private static UVAExchange exchangeToTest2;

	@BeforeAll
	public static void setup() {
		mapper = Jackson.newObjectMapper();
		exchangeToTest1 = new UVAExchange(LocalDate.of(EXCHANGE_1_YEAR, EXCHANGE_1_MONTH, EXCHANGE_1_DAY),
				EXCHANGE_1_RATE);
		exchangeToTest2 = new UVAExchange(LocalDate.of(EXCHANGE_2_YEAR, EXCHANGE_2_MONTH, EXCHANGE_2_DAY),
				EXCHANGE_2_RATE);

	}

	@Test
	public void testUvaExchangeSerializationShouldWork() throws JsonProcessingException {
		assertSerialization(mapper, exchangeToTest1, EXCHANGE_1_SERIALIZED_STRING);
	}

	@Test
	public void testUvaExchangeNullSerializationShouldWork() throws JsonProcessingException {
		assertSerialization(mapper, new UVAExchange(), EXCHANGE_NULL_SERIALIZED);
	}

	@Test
	public void testUvaExchangeDeserializationShouldWork() throws IOException {
		assertDeserialization(mapper, exchangeToTest1, EXCHANGE_1_SERIALIZED_STRING, UVAExchange.class);
	}

	@Test
	public void testUvaExchangeNullDeserializationShouldWork() throws IOException {
		assertDeserialization(mapper, new UVAExchange(), EXCHANGE_NULL_SERIALIZED, UVAExchange.class);
	}

	@Test
	public void testUvaExchangeListSerializationShouldWork() throws JsonProcessingException {
		List<UVAExchange> toSerialize = new ArrayList<UVAExchange>();
		toSerialize.add(exchangeToTest1);
		toSerialize.add(exchangeToTest2);
		assertSerialization(mapper, toSerialize, EXCHANGE_SERIALIZED_LIST_STRING);
	}

	@Test
	public void testUvaExchangeEmptyListSerializationShouldWork() throws JsonProcessingException {
		List<UVAExchange> toSerialize = new ArrayList<UVAExchange>();
		assertSerialization(mapper, toSerialize, EXCHANGE_SERIALIZED_EMPTY_LIST_STRING);
	}

	@Test
	public void testUvaExchangeNullListSerializationShouldWork() throws JsonProcessingException {
		List<UVAExchange> toSerialize = null;
		assertSerialization(mapper, toSerialize, EXCHANGE_LIST_NULL_SERIALIZED);
	}

	@Test
	public void testUvaExchangeListDeserializationShouldWork() throws IOException {
		List<UVAExchange> toSerialize = new ArrayList<UVAExchange>();
		toSerialize.add(exchangeToTest1);
		toSerialize.add(exchangeToTest2);
		assertListDeserialization(mapper, toSerialize, EXCHANGE_SERIALIZED_LIST_STRING,
				new TypeReference<List<UVAExchange>>() {
				});
	}

	@Test
	public void testUvaExchangeEmptyListDeserializationShouldWork() throws IOException {
		List<UVAExchange> toSerialize = new ArrayList<UVAExchange>();
		assertListDeserialization(mapper, toSerialize, EXCHANGE_SERIALIZED_EMPTY_LIST_STRING,
				new TypeReference<List<UVAExchange>>() {
				});
	}

	@Test
	public void testUvaExchangeNullListDeserializationShouldWork() throws IOException {
		List<UVAExchange> toSerialize = null;
		assertListDeserialization(mapper, toSerialize, EXCHANGE_LIST_NULL_SERIALIZED,
				new TypeReference<List<UVAExchange>>() {
				});
	}

}
