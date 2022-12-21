package hr.unipu

import org.testfx.framework.junit.ApplicationTest
import kotlin.Throws
import org.testfx.api.FxToolkit
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import hr.unipu.recipe.RecipeHandler
import hr.unipu.recipe.RecipeInterpreter
import hr.unipu.recipe.RecipeInterpreterJava
import javafx.application.Application
import org.junit.*
import org.testfx.api.FxAssert
import org.testfx.matcher.base.WindowMatchers
import java.lang.Exception

/**
 * Unit testing for Recipes: loading, running.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class C_RecipeTest : ApplicationTest() {

    /**
     * Will be called with `@Before` semantics, i.e. before each test method.
     */
    @Before
    fun main() {
        val appArgs = arrayOf("--simulationMode=true")
        val appClass: Class<*> = PlantComputerApplicationKotlin::class.java
        try {
            launch(appClass as Class<out Application>?, *appArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        FxToolkit.hideStage()
        release(*arrayOf<KeyCode>())
        release(*arrayOf<MouseButton>())
    }

    @Test
    fun test_recipe_is_loaded() {

        // GIVEN
        val fileName = "general_greens.json"
        RecipeInterpreter.loadFlexFormatRecipe(fileName)

        // THEN
        Assert.assertTrue(RecipeInterpreter.recipe != null)
    }

    @Test
    fun test_recipe_is_running() {
        // GIVEN
        val fileName = "general_greens.json"
        RecipeHandler.run_recipe(fileName)

        // THEN
        Assert.assertTrue(RecipeHandler.setpoints.isNotEmpty())
    }


}