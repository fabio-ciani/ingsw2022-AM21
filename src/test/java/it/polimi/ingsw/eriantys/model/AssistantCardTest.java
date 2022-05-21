package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssistantCardTest {

	@Test
	void value() {
		assertEquals(1, AssistantCard.CHEETAH.value());
		assertEquals(2, AssistantCard.OSTRICH.value());
		assertEquals(3, AssistantCard.CAT.value());
		assertEquals(4, AssistantCard.EAGLE.value());
		assertEquals(5, AssistantCard.FOX.value());
		assertEquals(6, AssistantCard.LIZARD.value());
		assertEquals(7, AssistantCard.OCTOPUS.value());
		assertEquals(8, AssistantCard.DOG.value());
		assertEquals(9, AssistantCard.ELEPHANT.value());
		assertEquals(10, AssistantCard.TURTLE.value());
	}

	@Test
	void movement() {
		assertEquals(1, AssistantCard.CHEETAH.movement());
		assertEquals(1, AssistantCard.OSTRICH.movement());
		assertEquals(2, AssistantCard.CAT.movement());
		assertEquals(2, AssistantCard.EAGLE.movement());
		assertEquals(3, AssistantCard.FOX.movement());
		assertEquals(3, AssistantCard.LIZARD.movement());
		assertEquals(4, AssistantCard.OCTOPUS.movement());
		assertEquals(4, AssistantCard.DOG.movement());
		assertEquals(5, AssistantCard.ELEPHANT.movement());
		assertEquals(5, AssistantCard.TURTLE.movement());
	}
}