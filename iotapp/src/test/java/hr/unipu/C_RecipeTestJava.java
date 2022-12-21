package hr.unipu;

import hr.unipu.recipe.RecipeHandlerJava;
import hr.unipu.recipe.RecipeInterpreterJava;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Unit testing for Recipes: loading, running.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
public class C_RecipeTestJava extends ApplicationTest {

    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     */
    @BeforeClass
    public static void main() {
        String[] appArgs = new String[]{"--simulationMode=true"};
        Class appClass = PlantComputerApplicationJava.class;
        try {
            ApplicationTest.launch(appClass, appArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void test_recipe_is_loaded() {

        // GIVEN
        String fileName = "general_greens.json";
        RecipeInterpreterJava.loadFlexFormatRecipe(fileName);

        // THEN
        Assert.assertTrue(RecipeInterpreterJava.recipe != null);
    }

    @Test
    public void test_recipe_is_running() {
        // GIVEN
        String fileName = "general_greens.json";
        RecipeHandlerJava.run_recipe(fileName);

        // THEN
        Assert.assertTrue(RecipeHandlerJava.setpoints != null);
    }

}
