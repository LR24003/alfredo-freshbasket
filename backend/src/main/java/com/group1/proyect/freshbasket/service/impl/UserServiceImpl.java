package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.UserRequestDTO;
import com.group1.proyect.freshbasket.dto.response.UserResponseDTO;
import com.group1.proyect.freshbasket.entity.Country;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.CountryRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends GenericServiceImpl<User, UserRequestDTO, UserResponseDTO, Long> implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CountryRepository countryRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CountryRepository countryRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.countryRepository = countryRepository;
    }


    @Override
    protected UserResponseDTO convertToResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());


        if (user.getCountry() != null) {
            dto.setCountryId(user.getCountry().getId());
            dto.setCountryName(user.getCountry().getName());
        }
        return dto;
    }

    @Override
    protected User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        if (dto.getCountryName() != null && !dto.getCountryName().trim().isEmpty()) {
            String countryName = dto.getCountryName().trim();
            Country country = countryRepository.findByNameIgnoreCase(countryName)
                    .orElseGet(() -> {
                        Country newCountry = new Country();
                        newCountry.setName(countryName);
                        String desc = countryName.length() >= 2 ? countryName.substring(0, 2).toUpperCase() : countryName.toUpperCase();
                        newCountry.setDescription(desc);
                        return countryRepository.save(newCountry);
                    });
            user.setCountry(country);
        }
        return user;
    }

    @Override
    protected void updateEntityFromDto(UserRequestDTO dto, User userExisting) {
        userExisting.setName(dto.getName());
        userExisting.setLastName(dto.getLastName());
        userExisting.setPhone(dto.getPhone());
        userExisting.setEmail(dto.getEmail());
        userExisting.setRole(dto.getRole());

        if (dto.getPassword() != null
                && !dto.getPassword().trim().isEmpty()
                && !dto.getPassword().equals("DUMMY_PASSWORD_NOT_CHANGED")) {
            userExisting.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getCountryName() != null && !dto.getCountryName().trim().isEmpty()) {
            Country country = countryRepository.findByNameIgnoreCase(dto.getCountryName().trim())
                    .orElseGet(() -> {
                        Country newCountry = new Country();
                        newCountry.setName(dto.getCountryName().trim());
                        return countryRepository.save(newCountry);
                    });
            userExisting.setCountry(country);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        return userRepository.findById(id)
                .filter(User::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese ID: " + id));
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO requestDTO) {
        if (requestDTO.getCountryName() == null || requestDTO.getCountryName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }

        User user = convertToEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO requestDTO) {
        User userExisting = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese ID: " + id));

        updateEntityFromDto(requestDTO, userExisting);
        User savedUser = userRepository.save(userExisting);
        return convertToResponseDto(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .filter(User::isActive)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));
        return convertToResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserProfileByEmail(String email, UserRequestDTO requestDTO) {
        User userExisting = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));

        userExisting.setName(requestDTO.getName());
        userExisting.setLastName(requestDTO.getLastName());
        userExisting.setPhone(requestDTO.getPhone());

        if (requestDTO.getPassword() != null
                && !requestDTO.getPassword().trim().isEmpty()
                && !requestDTO.getPassword().equals("DUMMY_PASSWORD_NOT_CHANGED")) {
            userExisting.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        if (requestDTO.getCountryName() != null && !requestDTO.getCountryName().trim().isEmpty()) {
            Country country = countryRepository.findByNameIgnoreCase(requestDTO.getCountryName().trim())
                    .orElseGet(() -> {
                        Country newCountry = new Country();
                        newCountry.setName(requestDTO.getCountryName().trim());
                        return countryRepository.save(newCountry);
                    });
            userExisting.setCountry(country);
        }

        User savedUser = userRepository.save(userExisting);
        return convertToResponseDto(savedUser);
    }
}