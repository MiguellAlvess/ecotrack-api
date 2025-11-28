package br.db.ecotrack.ecotrack_api.config;

import br.db.ecotrack.ecotrack_api.domain.entity.Material;
import br.db.ecotrack.ecotrack_api.domain.enums.MaterialType;
import br.db.ecotrack.ecotrack_api.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MaterialRepository materialRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (materialRepository.count() == 0) {
            System.out.println("Populando a tabela de materiais com dados iniciais...");

            Material plastic = new Material(null, MaterialType.PLASTIC.getTypeName(), "Garrafas PET, embalagens, sacolas.");
            Material glass = new Material(null, MaterialType.GLASS.getTypeName(), "Garrafas de vidro, potes, frascos.");
            Material metal = new Material(null, MaterialType.METAL.getTypeName(), "Latas de alumínio, aço, tampinhas.");
            Material paper = new Material(null, MaterialType.PAPER.getTypeName(), "Jornais, revistas, caixas de papelão.");
            Material organic = new Material(null, MaterialType.ORGANIC.getTypeName(), "Restos de alimentos, cascas de frutas.");

            List<Material> initialMaterials = Arrays.asList(plastic, glass, metal, paper, organic);

            materialRepository.saveAll(initialMaterials);

            System.out.println("Tabela de materiais populada com sucesso.");
        } else {
            System.out.println("A tabela de materiais já contém dados. Nenhuma ação foi tomada.");
        }
    }
}
