package edu.unimagdalena.cowork;

import edu.unimagdalena.cowork.domain.entities.*;
import edu.unimagdalena.cowork.domain.repositories.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("h2")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProducerProfileRepository producerProfileRepository;
    private final FarmRepository farmRepository;
    private final ProductRepository productRepository;
    private final ProductProcessRepository productProcessRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ProducerProfileRepository producerProfileRepository,
                           FarmRepository farmRepository,
                           ProductRepository productRepository,
                           ProductProcessRepository productProcessRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.producerProfileRepository = producerProfileRepository;
        this.farmRepository = farmRepository;
        this.productRepository = productRepository;
        this.productProcessRepository = productProcessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) {
            log.info("Seed data already exists, skipping.");
            return;
        }

        log.info("Seeding demo data...");

        // Buyer
        createBuyer("comprador@cafe.com", "cafe123456", "Comprador Demo", "+573001111111");

        // Credenciales de pago compartidas para demo
        String mpToken = "APP_USR-7786118604850582-052909-9952021c912f8837e93b470308723e87-3435704948";
        String mpKey = "APP_USR-ef0727c9-5bee-4549-a16e-e60274399be1";
        String nequiDemo = "3216992512";

        seedProducer("jheymer@cafe.com", "password123",
                "Jheymer Jhireth Navarro", "+573001234567",
                "J-Cafe", "Corregimiento de Sacramento, Fundacion",
                "Proyecto familiar que conecta el origen del cafe con el consumidor.",
                "Familia cafetera con transparencia y trazabilidad.",
                "15 anos", mpToken, mpKey, nequiDemo,
                "Los Limos de la Fe", "Sacramento, Fundacion, Magdalena",
                "Finca familiar en la Sierra Nevada, microlotes y cafe tostado.",
                new String[][]{
                        {"Cafe Arabica Sierra Nevada", "Arabica", "25000", "10", "Cafe de perfil dulce y limpio con practicas familiares y trazabilidad.", "https://images.unsplash.com/photo-1559056199-641a0ac8b8d5?w=900&h=700&fit=crop"},
                        {"Geisha de altura El Amanecer", "Geisha", "35000", "5", "Lote especial con notas florales y cuerpo ligero.", "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=900&h=700&fit=crop"},
                });

        seedProducer("roble@cafe.com", "password123",
                "Maria Elena Rojas", "+573002222222",
                "Finca El Roble", "Vereda San Pablo, Fundacion",
                "Cafe de altura organico con respeto por la biodiversidad.",
                "Desde 2010 produciendo cafe de especialidad sostenible.",
                "14 anos", mpToken, mpKey, nequiDemo,
                "El Roble", "Vereda San Pablo, Fundacion, Magdalena",
                "Finca de 8 hectareas a 1.400 msnm con sombra nativa.",
                new String[][]{
                        {"Bourbon microlote Sacramento", "Bourbon", "30000", "8", "Microlote frutal con acidez amable, ideal para filtrados.", "https://images.unsplash.com/photo-1514432324607-2e467f4af445?w=900&h=700&fit=crop"},
                        {"Caturra organico El Roble", "Caturra", "22000", "12", "Cafe balanceado con notas a chocolate, cultivo 100% organico.", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=900&h=700&fit=crop"},
                });

        seedProducer("montana@cafe.com", "password123",
                "Carlos Andres Mendoza", "+573003333333",
                "Cafe Montana Azul", "Sierra Nevada, Fundacion",
                "Microlotes de especialidad desde las alturas de la Sierra Nevada.",
                "Lotes pequenos con procesos controlados y perfil unico de region.",
                "8 anos", mpToken, mpKey, nequiDemo,
                "Montana Azul", "Sierra Nevada, Fundacion, Magdalena",
                "Finca a 1.600 msnm con microclima ideal para exoticas y procesos honey.",
                new String[][]{
                        {"Castillo Honey Montana Azul", "Castillo", "28000", "6", "Proceso honey con dulzor intenso y cuerpo medio-alto.", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=900&h=700&fit=crop"},
                        {"Tabi lavado de altura", "Tabi", "32000", "4", "Variedad Tabi de cosecha selectiva, acidez brillante.", "https://images.unsplash.com/photo-1507133750040-4a8f570215e5?w=900&h=700&fit=crop"},
                });

        seedProducer("asociacion@cafe.com", "password123",
                "Asociacion Cafetera Sacramento", "+573004444444",
                "ACS - Asociacion Cafetera", "Sacramento, Fundacion, Magdalena",
                "Cooperativa de 12 pequenos productores con calidad consistente.",
                "12 familias cafeteras unidas desde 2018 para venta directa.",
                "6 anos", mpToken, mpKey, nequiDemo,
                "Finca Colectiva Sacramento", "Sacramento, Fundacion",
                "Lotes de multiples fincas familiares, todos sobre 1.200 msnm.",
                new String[][]{
                        {"Premium Blend Sacramento", "Arabica Blend", "20000", "20", "Mezcla balanceada de lotes familiares, excelente relacion calidad-precio.", "https://images.unsplash.com/photo-1504630083234-14187a9df0f5?w=900&h=700&fit=crop"},
                });

        log.info("Seed complete: 1 buyer, 4 producers, 4 farms, 7 products.");
    }

    private void createBuyer(String email, String password, String name, String phone) {
        User buyer = new User();
        buyer.setFullName(name);
        buyer.setEmail(email);
        buyer.setPasswordHash(passwordEncoder.encode(password));
        buyer.setPhone(phone);
        buyer.setWhatsappNumber(phone);
        buyer.setRole(RoleName.USER);
        buyer.setEnabled(true);
        userRepository.save(buyer);
    }

    private void seedProducer(String email, String password,
                              String fullName, String phone,
                              String brand, String location,
                              String bio, String story, String experience,
                              String mpToken, String mpKey, String nequi,
                              String farmName, String farmLocation, String farmDesc,
                              String[][] products) {

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setWhatsappNumber(phone);
        user.setRole(RoleName.USER);
        user.setEnabled(true);
        userRepository.save(user);

        ProducerProfile profile = new ProducerProfile();
        profile.setUser(user);
        profile.setActiveSeller(true);
        profile.setBrandName(brand);
        profile.setBio(bio);
        profile.setStory(story);
        profile.setLocationText(location);
        profile.setYearsExperience(experience);
        profile.setMercadopagoAccessToken(mpToken);
        profile.setMercadopagoPublicKey(mpKey);
        profile.setNequiPhone(nequi);
        producerProfileRepository.save(profile);

        Farm farm = new Farm();
        farm.setProducerProfile(profile);
        farm.setName(farmName);
        farm.setLocationText(farmLocation);
        farm.setDescription(farmDesc);
        farm.setActive(true);
        farmRepository.save(farm);

        for (String[] p : products) {
            seedProduct(profile, farm, p[0], p[1],
                    new BigDecimal(p[2]), new BigDecimal(p[3]),
                    p[4], p[5],
                    List.of("SIEMBRA", "CULTIVO", "COSECHA", "LAVADO_SECADO"));
        }
    }

    private void seedProduct(ProducerProfile profile, Farm farm,
                             String name, String variety,
                             BigDecimal pricePerKg, BigDecimal availableKg,
                             String description, String imageUrl,
                             List<String> stages) {
        Product product = new Product();
        product.setProducerProfile(profile);
        product.setFarm(farm);
        product.setName(name);
        product.setVariety(variety);
        product.setPricePerKg(pricePerKg);
        product.setAvailableKg(availableKg);
        product.setDescription(description);
        product.setMainImageUrl(imageUrl);
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);

        String[] results = {"Cafe variedad Arabica", "Planta fortalecida", "Cereza madura", "Grano pergamino"};
        String[] descriptions = {
                "Seleccion de semilla, preparacion del lote y siembra en condiciones de altura y sombra.",
                "Manejo del cultivo con cuidado del suelo, seguimiento del clima y control manual.",
                "Recoleccion manual de frutos en su punto ideal para mantener calidad uniforme.",
                "Lavado del grano y secado controlado para lograr limpieza, estabilidad y mejor taza."
        };
        int idx = 0;
        for (String stage : stages) {
            ProductProcess process = new ProductProcess();
            process.setProduct(product);
            process.setStage(ProductProcessStage.valueOf(stage.toUpperCase()));
            process.setDescription(descriptions[idx]);
            process.setResultType(results[idx]);
            process.setOrderIndex(idx);
            productProcessRepository.save(process);
            idx++;
        }
    }
}
