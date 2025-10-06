package com.quetoquenana.personservice;

import com.quetoquenana.personservice.controller.PersonController;
import com.quetoquenana.personservice.controller.PersonProfileController;
import com.quetoquenana.personservice.service.PersonProfileService;
import com.quetoquenana.personservice.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest({PersonController.class, PersonProfileController.class})
class PersonServiceApplicationTests {

    @MockBean
    private PersonService personService;

    @MockBean
    private PersonProfileService personProfileService;

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonProfileController personProfileController;

	@Test
	void contextLoads() {
        assertThat(personController).isNotNull();
        assertThat(personProfileController).isNotNull();
	}

}
