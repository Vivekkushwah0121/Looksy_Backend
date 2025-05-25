package com.Looksy.Backend.controller;

import com.Looksy.Backend.dto.DimensionDTO;
import com.Looksy.Backend.model.Dimension;
import com.Looksy.Backend.service.DimensionService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dimensions")
public class DimensionController {
    private final DimensionService dimensionService;

    @Autowired
    public DimensionController(DimensionService dimensionService) {
        this.dimensionService = dimensionService;
    }

    // Helper method to convert Dimension to DimensionDTO
    private DimensionDTO convertToDTO(Dimension dimension) {
        DimensionDTO dto = new DimensionDTO();
        dto.setDimensionid(dimension.getDimensionid());
        dto.setCategoryid(dimension.getCategoryid());
        dto.setSubcategoryid(dimension.getSubcategoryid());
        dto.setProductid(dimension.getProductid());
        dto.setDimension(dimension.getDimension());
        return dto;
    }

    // Helper method to convert DimensionDTO to Dimension
    private Dimension convertToEntity(DimensionDTO dto) {
        Dimension dimension = new Dimension(dto.getCategoryid(), dto.getSubcategoryid(), dto.getProductid(), dto.getDimension());
        // Set id only if present
        if (dto.getDimensionid() != null) {
            dimension.setDimensionid(dto.getDimensionid());
        }
        return dimension;
    }

    @PostMapping("/add_new_dimension")
    public ResponseEntity<DimensionDTO> addNewDimension(@RequestBody DimensionDTO dimensionDTO) {
        Dimension dimension = convertToEntity(dimensionDTO);
        Dimension newDimension = dimensionService.addNewDimension(dimension);
        DimensionDTO responseDTO = convertToDTO(newDimension);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/display_all_dimensions")
    public ResponseEntity<List<DimensionDTO>> displayAllDimensions() {
        List<Dimension> dimensions = dimensionService.displayAllDimensions();
        List<DimensionDTO> dimensionDTOs = dimensions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dimensionDTOs);
    }

    @PostMapping("/edit_dimension_data")
    public ResponseEntity<DimensionDTO> editDimensionData(@RequestBody DimensionDTO dimensionDTO) {
        Dimension dimension = convertToEntity(dimensionDTO);
        Dimension updatedDimension = dimensionService.editDimensionData(dimension);
        DimensionDTO responseDTO = convertToDTO(updatedDimension);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/delete_dimension_data")
    public ResponseEntity<?> deleteDimensionData(@RequestParam ObjectId dimensionid) {
        Dimension deletedDimension = dimensionService.deleteDimensionData(dimensionid);
        if (deletedDimension != null) {
            return ResponseEntity.ok().body(
                    String.format("Dimension with ID %s and value '%s' deleted successfully.",
                            deletedDimension.getDimensionid(), deletedDimension.getDimension())
            );
        } else {
            return ResponseEntity.status(404).body("Dimension not found.");
        }
    }
    @PostMapping("/fetch_all_dimensions")
    public ResponseEntity<List<DimensionDTO>> fetchAllDimensions(@RequestBody DimensionDTO dimensionDTO) {
        List<Dimension> dimensions = dimensionService.fetchAllDimensions(
                dimensionDTO.getCategoryid(),
                dimensionDTO.getSubcategoryid(),
                dimensionDTO.getProductid()
        );
        List<DimensionDTO> dimensionDTOs = dimensions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dimensionDTOs);
    }


}
