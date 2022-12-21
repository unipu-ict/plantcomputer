package hr.unipu.plantcomputer;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Recipe Data-Transfer-Object (DTO)
 * - for mapping JSON recipes as Java objects.
 */
public class RecipeDTOJava {
    private String _id;
    private String author;
    private List<String> certified_by;
    private String date_created;
    private Integer downloads;
    private String format;
    private List<String> optimization;

    private List<Phases> phases = new ArrayList<>();
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phases {
        private Integer cycles;
        private String name;
        private Step step;

        public static class Step {
            private List<AirTemperature> air_temperature = new ArrayList<>();

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class AirTemperature {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "air_temperature";
                }
            }

            private List<LightIntensityBlue> light_intensity_blue = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LightIntensityBlue {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "light_intensity_blue";
                }
            }

            private List<LightIntensityRed> light_intensity_red = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LightIntensityRed {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "light_intensity_red";
                }
            }

            private List<LightIntensityWhite> light_intensity_white = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LightIntensityWhite {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "light_intensity_white";
                }
            }

            private List<LightIlluminance> light_illuminance = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LightIlluminance {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "light_illuminance";
                }
            }

            private List<Nutrient_FloraDuoA> nutrient_flora_duo_a = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Nutrient_FloraDuoA {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "nutrient_flora_duo_a";
                }
            }

            private List<Nutrient_FloraDuoB> nutrient_flora_duo_b = new ArrayList<>();
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Nutrient_FloraDuoB {
                private Long end_time;
                private Long start_time;
                private Double value;

                // GETTERS & SETTERS
                public Long getEnd_time() {
                    return end_time;
                }
                public void setEnd_time(Long end_time) {
                    this.end_time = end_time;
                }
                public Long getStart_time() {
                    return start_time;
                }
                public void setStart_time(Long start_time) {
                    this.start_time = start_time;
                }
                public Double getValue() {
                    return value;
                }
                public void setValue(Double value) {
                    this.value = value;
                }

                // OTHER
                public String getName() {
                    return "nutrient_flora_duo_b";
                }
            }

            // GETTERS & SETTERS
            public List<AirTemperature> getAir_temperature() {
                return air_temperature;
            }
            public void setAir_temperature(List<AirTemperature> air_temperature) {
                this.air_temperature = air_temperature;
            }
            public List<LightIntensityBlue> getLight_intensity_blue() {
                return light_intensity_blue;
            }
            public void setLight_intensity_blue(List<LightIntensityBlue> light_intensity_blue) {
                this.light_intensity_blue = light_intensity_blue;
            }
            public List<LightIntensityRed> getLight_intensity_red() {
                return light_intensity_red;
            }
            public void setLight_intensity_red(List<LightIntensityRed> light_intensity_red) {
                this.light_intensity_red = light_intensity_red;
            }
            public List<LightIlluminance> getLight_illuminance() {
                return light_illuminance;
            }
            public void setLight_illuminance(List<LightIlluminance> light_illuminance) {
                this.light_illuminance = light_illuminance;
            }
            public List<Nutrient_FloraDuoA> getNutrient_flora_duo_a() {
                return nutrient_flora_duo_a;
            }
            public void setNutrient_flora_duo_a(List<Nutrient_FloraDuoA> nutrient_flora_duo_a) {
                this.nutrient_flora_duo_a = nutrient_flora_duo_a;
            }
            public List<Nutrient_FloraDuoB> getNutrient_flora_duo_b() {
                return nutrient_flora_duo_b;
            }
            public void setNutrient_flora_duo_b(List<Nutrient_FloraDuoB> nutrient_flora_duo_b) {
                this.nutrient_flora_duo_b = nutrient_flora_duo_b;
            }

            // OTHER
            public List<List<?>> items() {
                List<List<?>> items = new LinkedList<>();
                if (air_temperature != null)
                    items.add(air_temperature);
                if (light_intensity_blue != null)
                    items.add(light_intensity_blue);
                if (light_intensity_red != null)
                    items.add(light_intensity_red);
                if (light_illuminance != null)
                    items.add(light_illuminance);
                if (nutrient_flora_duo_a != null)
                    items.add(nutrient_flora_duo_a);
                if (nutrient_flora_duo_b != null)
                    items.add(nutrient_flora_duo_b);

                return items;
            }
        }

        private String time_units = "hours";   // default hours

        private VariableUnits variable_units;
        public static class VariableUnits {
            private String air_temperature;
            private String light_illuminance;
            private String nutrient_flora_duo_a;
            private String nutrient_flora_duo_b;

            // GETTERS & SETTERS
            public String getAir_temperature() {
                return air_temperature;
            }
            public void setAir_temperature(String air_temperature) {
                this.air_temperature = air_temperature;
            }
            public String getLight_illuminance() {
                return light_illuminance;
            }
            public void setLight_illuminance(String light_illuminance) {
                this.light_illuminance = light_illuminance;
            }
            public String getNutrient_flora_duo_a() {
                return nutrient_flora_duo_a;
            }
            public void setNutrient_flora_duo_a(String nutrient_flora_duo_a) {
                this.nutrient_flora_duo_a = nutrient_flora_duo_a;
            }
            public String getNutrient_flora_duo_b() {
                return nutrient_flora_duo_b;
            }
            public void setNutrient_flora_duo_b(String nutrient_flora_duo_b) {
                this.nutrient_flora_duo_b = nutrient_flora_duo_b;
            }

        }

        // GETTERS & SETTERS
        public Integer getCycles() {
            return cycles;
        }
        public void setCycles(Integer cycles) {
            this.cycles = cycles;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Step getStep() {
            return step;
        }
        public void setStep(Step step) {
            this.step = step;
        }
        public String getTime_units() {
            return time_units;
        }
        public void setTime_units(String time_units) {
            this.time_units = time_units;
        }
        public VariableUnits getVariable_units() {
            return variable_units;
        }
        public void setVariable_units(VariableUnits variable_units) {
            this.variable_units = variable_units;
        }
    }

    private List<Operations> operations;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Operations {
        private String name;
        private Integer cycles;
        private DayNight day, night;

        public static class DayNight {
            private Long hours;
            private Double air_temperature;
            private Double nutrient_flora_duo_a;
            private Double nutrient_flora_duo_b;
            private Double light_intensity_red;
            private Double light_intensity_white;
            private Double light_intensity_blue;

            // GETTERS & SETTERS
            public Long getHours() {
                return hours;
            }
            public void setHours(Long hours) {
                this.hours = hours;
            }
            public Double getAir_temperature() {
                return air_temperature;
            }
            public void setAir_temperature(Double air_temperature) {
                this.air_temperature = air_temperature;
            }
            public Double getNutrient_flora_duo_a() {
                return nutrient_flora_duo_a;
            }
            public void setNutrient_flora_duo_a(Double nutrient_flora_duo_a) {
                this.nutrient_flora_duo_a = nutrient_flora_duo_a;
            }
            public Double getNutrient_flora_duo_b() {
                return nutrient_flora_duo_b;
            }
            public void setNutrient_flora_duo_b(Double nutrient_flora_duo_b) {
                this.nutrient_flora_duo_b = nutrient_flora_duo_b;
            }
            public Double getLight_intensity_red() {
                return light_intensity_red;
            }
            public void setLight_intensity_red(Double light_intensity_red) {
                this.light_intensity_red = light_intensity_red;
            }
            public Double getLight_intensity_white() {
                return light_intensity_white;
            }
            public void setLight_intensity_white(Double light_intensity_white) {
                this.light_intensity_white = light_intensity_white;
            }
            public Double getLight_intensity_blue() {
                return light_intensity_blue;
            }
            public void setLight_intensity_blue(Double light_intensity_blue) {
                this.light_intensity_blue = light_intensity_blue;
            }

            // OTHER
        }

        // GETTERS & SETTERS
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getCycles() {
            return cycles;
        }
        public void setCycles(Integer cycles) {
            this.cycles = cycles;
        }
        public DayNight getDay() {
            return day;
        }
        public void setDay(DayNight day) {
            this.day = day;
        }
        public DayNight getNight() {
            return night;
        }
        public void setNight(DayNight night) {
            this.night = night;
        }
    }

    // HELPER
    public void mapOperations2Phases() {
        Long time_counter = 0L;
        for (Operations operation : this.operations) {
            Phases phase = new Phases();
            phase.name = operation.name;
            phase.cycles = operation.cycles;

            Phases.Step step = new Phases.Step();
            phase.step = step;

            // AirTemperature
            Phases.Step.AirTemperature air_temperature;
            Phases.Step.Nutrient_FloraDuoA nutrient_flora_duo_a;
            Phases.Step.Nutrient_FloraDuoB nutrient_flora_duo_b;
            Phases.Step.LightIntensityRed light_intensity_red;
            Phases.Step.LightIntensityBlue light_intensity_blue;
            Phases.Step.LightIntensityWhite light_intensity_white;

            air_temperature = new Phases.Step.AirTemperature();
            nutrient_flora_duo_a = new Phases.Step.Nutrient_FloraDuoA();
            nutrient_flora_duo_b = new Phases.Step.Nutrient_FloraDuoB();
            light_intensity_red = new Phases.Step.LightIntensityRed();
            light_intensity_blue = new Phases.Step.LightIntensityBlue();
            light_intensity_white = new Phases.Step.LightIntensityWhite();

            // day
            air_temperature.start_time = time_counter;
            nutrient_flora_duo_a.start_time = time_counter;
            nutrient_flora_duo_b.start_time = time_counter;
            light_intensity_red.start_time = time_counter;
            light_intensity_blue.start_time = time_counter;
            light_intensity_white.start_time = time_counter;

            time_counter = time_counter + operation.day.getHours();

            air_temperature.end_time = time_counter;
            nutrient_flora_duo_a.end_time = time_counter;
            nutrient_flora_duo_b.end_time = time_counter;
            light_intensity_red.end_time = time_counter;
            light_intensity_blue.end_time = time_counter;
            light_intensity_white.end_time = time_counter;

            air_temperature.value = operation.day.air_temperature;
            nutrient_flora_duo_a.value = operation.day.nutrient_flora_duo_a;
            nutrient_flora_duo_b.value = operation.day.nutrient_flora_duo_b;
            light_intensity_red.value = operation.day.light_intensity_red;
            light_intensity_blue.value = operation.day.light_intensity_blue;
            light_intensity_white.value = operation.day.light_intensity_white;

            step.air_temperature.add(air_temperature);
            step.nutrient_flora_duo_a.add(nutrient_flora_duo_a);
            step.nutrient_flora_duo_b.add(nutrient_flora_duo_b);
            step.light_intensity_red.add(light_intensity_red);
            step.light_intensity_blue.add(light_intensity_blue);
            step.light_intensity_white.add(light_intensity_white);


            // AirTemperature (clear);
            air_temperature = new Phases.Step.AirTemperature();
            nutrient_flora_duo_a = new Phases.Step.Nutrient_FloraDuoA();
            nutrient_flora_duo_b = new Phases.Step.Nutrient_FloraDuoB();
            light_intensity_red = new Phases.Step.LightIntensityRed();
            light_intensity_blue = new Phases.Step.LightIntensityBlue();
            light_intensity_white = new Phases.Step.LightIntensityWhite();

            // night
            air_temperature.start_time = time_counter;
            nutrient_flora_duo_a.start_time = time_counter;
            nutrient_flora_duo_b.start_time = time_counter;
            light_intensity_red.start_time = time_counter;
            light_intensity_blue.start_time = time_counter;
            light_intensity_white.start_time = time_counter;

            time_counter = time_counter + operation.night.getHours();

            air_temperature.end_time = time_counter;
            nutrient_flora_duo_a.end_time = time_counter;
            nutrient_flora_duo_b.end_time = time_counter;
            light_intensity_red.end_time = time_counter;
            light_intensity_blue.end_time = time_counter;
            light_intensity_white.end_time = time_counter;

            air_temperature.value = operation.night.air_temperature;
            nutrient_flora_duo_a.value = operation.night.nutrient_flora_duo_a;
            nutrient_flora_duo_b.value = operation.night.nutrient_flora_duo_b;
            light_intensity_red.value = operation.night.light_intensity_red;
            light_intensity_blue.value = operation.night.light_intensity_blue;
            light_intensity_white.value = operation.night.light_intensity_white;

            step.air_temperature.add(air_temperature);
            step.nutrient_flora_duo_a.add(nutrient_flora_duo_a);
            step.nutrient_flora_duo_b.add(nutrient_flora_duo_b);
            step.light_intensity_red.add(light_intensity_red);
            step.light_intensity_blue.add(light_intensity_blue);
            step.light_intensity_white.add(light_intensity_white);

            this.phases.add(phase);
        }
    }

    private List<String> plant_type;
    private String rating;
    private List<String> seeds;
    private String version;


    // --- GETTERS AND SETTERS
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public List<String> getCertified_by() {
        return certified_by;
    }
    public void setCertified_by(List<String> certified_by) {
        this.certified_by = certified_by;
    }
    public String getDate_created() {
        return date_created;
    }
    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
    public Integer getDownloads() {
        return downloads;
    }
    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public List<String> getOptimization() {
        return optimization;
    }
    public void setOptimization(List<String> optimization) {
        this.optimization = optimization;
    }
    public List<Phases> getPhases() {
        return phases;
    }
    public void setPhases(List<Phases> phases) {
        this.phases = phases;
    }
    public List<String> getPlant_type() {
        return plant_type;
    }
    public void setPlant_type(List<String> plant_type) {
        this.plant_type = plant_type;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public List<String> getSeeds() {
        return seeds;
    }
    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public List<Operations> getOperations() {
        return operations;
    }
    public void setOperations(List<Operations> operations) {
        this.operations = operations;
    }
}



