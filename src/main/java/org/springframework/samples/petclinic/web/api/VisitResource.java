/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web.api;

import javax.validation.Valid;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@RestController
public class VisitResource extends AbstractResourceController {

	private final ClinicService clinicService;

	@Autowired
	public VisitResource(ClinicService clinicService) {
		this.clinicService = clinicService;
	}

	@GetMapping(value="/visits/list")
    public Collection<Visit> showVetVisitList(@RequestParam("vetId") int vetId) {
        return this.clinicService.findVisitsByVetId(vetId);
    }

    @DeleteMapping("/visits/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVisit(@PathVariable("visitId") int visitId){
	    this.clinicService.deleteVisit(visitId);
    }

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void create(@PathVariable("petId") int petId, @RequestBody
        Visit visit, @RequestParam("vetId") int vetId, BindingResult
                       bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new InvalidRequestException("Visit is invalid", bindingResult);
		}

		final Pet pet = clinicService.findPetById(petId);
		if (pet == null) {
			throw new BadRequestException("Pet with Id '" + petId + "' is unknown.");
		}

		final Vet vet = clinicService.findVetById(vetId);
		visit.setVet(vet);

		pet.addVisit(visit);

		clinicService.saveVisit(visit);
	}
}
