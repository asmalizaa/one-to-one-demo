package com.example.onetoonedemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstructorController {

	// inject repositories
	@Autowired
	private InstructorRepository instructorRepository;

	@Autowired
	private InstructorDetailRepository instDetailRepository;

	@GetMapping("/instructors")
	public List<Instructor> getInstructors() {
		return instructorRepository.findAll(); // find() - select
	}

	@GetMapping("/instructors/{id}")
	public ResponseEntity<Optional<Instructor>> getInstructorById(@PathVariable(value = "id") Long instructorId)
			throws Exception {
		Optional<Instructor> user = instructorRepository.findById(instructorId);
		return ResponseEntity.ok().body(user);
	}

	@PostMapping("/instructors")
	public Instructor createUser(@RequestBody Instructor instructor) {
		return instructorRepository.save(instructor);
	}

	@PutMapping("/instructors/{id}")
	public ResponseEntity<Instructor> updateUser(@PathVariable(value = "id") Long instructorId,
			@RequestBody InstructorDetail userDetails) throws Exception {

		// find if instructor is existed
		Optional<Instructor> instructor = instructorRepository.findById(instructorId);

		// if instructor existed, then can add details
		if (instructor.isPresent()) {
			// check if details existed
			Instructor _instructor = instructor.get();

			// if yes, the update existing details
			if (_instructor.getInstructorDetail() != null) {
				// get instructor detail id
				Optional<InstructorDetail> _details = instDetailRepository
						.findById(_instructor.getInstructorDetail().getId());
				// update existing detail
				if (_details.isPresent()) {
					InstructorDetail det = _details.get();
					det.setYoutubeChannel(userDetails.getYoutubeChannel());
					det.setHobby(userDetails.getHobby());
					instDetailRepository.save(det);
				}
			} else {
				// else, create new detail entry
				InstructorDetail _details = new InstructorDetail();
				_details.setYoutubeChannel(userDetails.getYoutubeChannel());
				_details.setHobby(userDetails.getHobby());
				_instructor.setInstructorDetail(_details);
			}

			final Instructor updatedUser = instructorRepository.save(_instructor);
			return new ResponseEntity<>(updatedUser, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	// delete 1 instructor record
	@DeleteMapping("/instructors/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long instructorId) throws Exception {

		// check if instructor existed
		Optional<Instructor> instructor = instructorRepository.findById(instructorId);

		if (instructor.isPresent()) {
			Instructor _instructor = instructor.get();
			instructorRepository.delete(_instructor);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	// delete all records
	@DeleteMapping("/instructors")
	public ResponseEntity<HttpStatus> deleteAllUsers() {
		try {
			instructorRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
