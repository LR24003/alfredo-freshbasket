package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.request.ExitRequestDTO;
import com.group1.proyect.freshbasket.dto.response.ExitResponseDTO;
import com.group1.proyect.freshbasket.entity.Exit;
import com.group1.proyect.freshbasket.entity.Product;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.repository.ExitRepository;
import com.group1.proyect.freshbasket.repository.ProductRepository;
import com.group1.proyect.freshbasket.repository.UserRepository;
import com.group1.proyect.freshbasket.service.ExitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExitServiceImpl extends GenericServiceImpl<Exit,
        ExitRequestDTO, ExitResponseDTO, Long> implements ExitService {

    private final ExitRepository exitRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ExitServiceImpl(
            ExitRepository exitRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        super(exitRepository);
        this.exitRepository = exitRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected ExitResponseDTO convertToResponseDto(Exit exit) {
        ExitResponseDTO dto = new ExitResponseDTO();
        dto.setId(exit.getId());
        dto.setExitDate(exit.getExitDate());
        dto.setQuantity(exit.getQuantity());

        if (exit.getProduct() != null) {
            dto.setProductId(exit.getProduct().getId());
            dto.setProductName(exit.getProduct().getName());
        } else {
            dto.setProductName("Sin producto asignado");
        }

        if (exit.getUser() != null) {
            dto.setUserId(exit.getUser().getId());
            String uName = exit.getUser().getName() != null ? exit.getUser().getName() : "";
            String uLastName = exit.getUser().getLastName() != null ? exit.getUser().getLastName() : "";
            String uFullName = (uName + " " + uLastName).trim();
            dto.setUserName(!uFullName.isEmpty() ? uFullName : "Usuario " + exit.getUser().getId());
        } else {
            dto.setUserName("Sin usuario asignado");
        }

        return dto;
    }

    @Override
    protected Exit convertToEntity(ExitRequestDTO dto) {
        Exit exit = new Exit();
        exit.setQuantity(dto.getQuantity());
        mapRelationsFromDto(dto, exit);
        return exit;
    }

    @Override
    protected void updateEntityFromDto(ExitRequestDTO dto, Exit exit) {
        mapRelationsFromDto(dto, exit);
    }

    private void mapRelationsFromDto(ExitRequestDTO dto, Exit exit) {
        String cleanProductName = dto.getProductName() != null ? dto.getProductName().trim() : "";
        Product product = productRepository.findByNameIgnoreCase(cleanProductName)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ese nombre: " + dto.getProductName()));

        String cleanUserName = dto.getUserName() != null ? dto.getUserName().trim() : "";
        User user = userRepository.findByFullNameIgnoreCase(cleanUserName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el nombre completo: " + dto.getUserName()));

        exit.setProduct(product);
        exit.setUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExitResponseDTO> getAll() {
        return exitRepository.findByActiveTrue()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExitResponseDTO getById(Long id) {
        return exitRepository.findById(id)
                .filter(Exit::isActive)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Salida no encontrada con ese ID: " + id));
    }

    @Override
    @Transactional
    public ExitResponseDTO create(ExitRequestDTO requestDTO) {
        Exit exit = convertToEntity(requestDTO);
        Product product = exit.getProduct();

        if (product != null) {
            if (!product.isActive()) {
                throw new IllegalStateException("No se pueden registrar salidas para un producto eliminado.");
            }

            int nuevoStock = product.getCurrentStock() - exit.getQuantity();
            if (nuevoStock < 0) {
                throw new IllegalStateException("Stock insuficiente para realizar la salida");
            }
            product.setCurrentStock(nuevoStock);
            productRepository.save(product);
        }

        Exit savedExit = exitRepository.save(exit);
        return convertToResponseDto(savedExit);
    }

    @Override
    @Transactional
    public ExitResponseDTO update(Long id, ExitRequestDTO requestDTO) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salida no encontrada con ese ID: " + id));

        updateEntityFromDto(requestDTO, exit);
        Product product = exit.getProduct();

        if (!product.isActive()) {
            throw new IllegalStateException("No se puede modificar esta salida porque el producto asociado está eliminado.");
        }

        int cantidadAnterior = exit.getQuantity();
        int cantidadNueva = requestDTO.getQuantity();
        int diferencia = cantidadNueva - cantidadAnterior;

        int nuevoStock = product.getCurrentStock() - diferencia;
        if (nuevoStock < 0) {
            throw new IllegalStateException("Stock insuficiente para actualizar la salida");
        }
        product.setCurrentStock(nuevoStock);
        productRepository.save(product);

        exit.setQuantity(cantidadNueva);

        Exit updated = exitRepository.save(exit);
        return convertToResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Exit exit = exitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salida no encontrada con ese ID: " + id));

        Product product = exit.getProduct();
        if (product != null) {
            if (!product.isActive()) {
                throw new IllegalStateException("No se puede eliminar esta salida porque pertenece a un producto eliminado.");
            }

            int nuevoStock = product.getCurrentStock() + exit.getQuantity();
            product.setCurrentStock(nuevoStock);
            productRepository.save(product);
        }

        exit.setActive(false);
        exitRepository.save(exit);
    }
}