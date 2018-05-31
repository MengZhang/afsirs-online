package org.afsirs.module;

import java.io.File;
import lombok.Data;

/**
 *
 * @author Meng Zhang
 */
@Data
public class SimOutput {
    
    private double totalArea = 0.0;
    private double plantedArea = 0.0;
    
    private File summaryFile, summaryFileExcel, calculationExcel;
}
