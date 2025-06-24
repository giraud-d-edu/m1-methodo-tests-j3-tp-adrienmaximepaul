package com.ynov.testing;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CalculatriceTest {
    @Nested
    public class TestAddition {
        @Test
        @DisplayName("Should add two number")
        public void add_1_plus_1_should_return_2() {
            // Given
            Calculatrice calc = new Calculatrice();
            calc.count = 14;

            // When
            int res = calc.addition(1, 1);

            // Then
            assertEquals(15, calc.count);
            assert (res == 2);
        }

        @Test
        @DisplayName("Should return erreur if number < 0")
        public void add_moins_1_plus_1_should_return_erreur() {
            // Given
            Calculatrice calc = new Calculatrice();
            calc.count = 14;
            // Then
            assertThatThrownBy(() -> calc.addition(-1, 1)).isInstanceOf(IllegalArgumentException.class).hasMessage("Addition parameters must be positive");
            assertEquals(14, calc.count);
        }

        @Test
        @DisplayName("Should return erreur count > 100")
        public void maximum_100_operation_should_return_erreur() {
            // Given
            Calculatrice calc = new Calculatrice();
            calc.count = 100;

            // Then
            assertThatThrownBy(() -> calc.addition(1, 1)).isInstanceOf(IllegalArgumentException.class).hasMessage("Reach the limit of 100 iteration");
            assertEquals(100, calc.count);
        }

        @Test
        @DisplayName("Should return erreur if count = 100")
        public void maximum_100_operation_should_return_ok() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When
            calc.count = 99;
            int result = calc.addition(1, 1);
            // Then
            assertEquals(100, calc.count);
            assert (result == 2);
        }
    }


    // ================== RÈGLES MÉTIER PROGRESSIVES ==================

    // Nouvelle règle : la calculatrice ne peut effectuer qu'un maximum de 100 opérations
    // Etape 4 : Implémenter un test ou plusieurs tests pour cette nouvelle règle
    // RED - Écrire un test qui effectue 100 opérations puis vérifie qu'une exception est levée à la 101ème
    // Etape 5 : Implémenter le code source pour que le(s) test(s) passe(nt)
    // GREEN - Ajouter un compteur d'opérations et la vérification de limite

    // ================== NOUVELLES MÉTHODES ==================

    // Nouvelle méthode : SOUSTRACTION
    // Etape 6 : Écrire un test pour la soustraction de base (ex: 5 - 3 = 2)
    // RED - Le test échoue car la méthode soustraction() n'existe pas
    // Etape 7 : Implémenter la méthode soustraction() basique
    // GREEN - return a - b;

    // Nouvelle règle pour soustraction : le résultat ne peut pas être négatif
    // Etape 8 : Écrire un test vérifiant qu'une exception est levée si résultat < 0
    // RED - Le test échoue car aucune vérification n'est faite
    // Etape 9 : Modifier soustraction() pour lever une exception si résultat négatif
    // GREEN - Vérifier le résultat avant de le retourner

    @Nested
    public class TestMultiplication {

        @Test
        @DisplayName("should mult 4 and 3")
        public void test_mult_4_and_3() {
            Calculatrice calculatrice = new Calculatrice();

            float res = calculatrice.multiplication(4f, 3f);

            assertEquals(res, 12f);
            assertEquals(calculatrice.count, 1);
        }

        @Test
        @DisplayName("should not multi by negative number")
        public void test_mult_negative_number() {
            Calculatrice calculatrice = new Calculatrice();

            assertThatThrownBy(() -> calculatrice.multiplication(-10f, 3f))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You can't multiply by negative number");

            assertThatThrownBy(() -> calculatrice.multiplication(3f, -10f))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You can't multiply by negative number");

            assertEquals(calculatrice.count, 0);
        }

        @Test
        @DisplayName("should not multi by 0")
        public void test_mult_0() {
            Calculatrice calculatrice = new Calculatrice();

            assertThatThrownBy(() -> calculatrice.multiplication(0f, 3f))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You can't multiply by 0");

            assertThatThrownBy(() -> calculatrice.multiplication(3f, 0f))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You can't multiply by 0");

            assertEquals(calculatrice.count, 0);
        }

        @Test
        @DisplayName("shound result to be less than 1000")
        public void test_shound_result_to_be_less_than_1000() {
            Calculatrice calculatrice = new Calculatrice();

            assertThatThrownBy(() -> calculatrice.multiplication(1001f, 1f))
                    .isInstanceOf(ArithmeticException.class)
                    .hasMessage("Result need to be less than 1000");

            assertEquals(calculatrice.count, 0);
        }


        @Test
        @DisplayName("Should return erreur count > 100")
        public void maximum_100_operation_should_return_erreur() {
            // Given
            Calculatrice calc = new Calculatrice();
            calc.count = 100;

            // Then
            assertThatThrownBy(() -> calc.multiplication(1f, 1f)).isInstanceOf(IllegalArgumentException.class).hasMessage("Reach the limit of 100 iteration");
            assertEquals(100, calc.count);
        }

        @Test
        @DisplayName("Should return erreur if count = 100")
        public void maximum_100_operation_should_return_ok() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When
            calc.count = 99;
            float result = calc.multiplication(1f, 1f);
            // Then
            assertEquals(100, calc.count);
            assert (result == 1);
        }
    }



    @Nested
    public class TestDivition{
        @Test
        @DisplayName("Should divide two numbers")
        public void divide_10_by_2_should_return_5() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When
            double res = calc.division(10.0, 2.0);

            // Then
            assert(res == 5.0);
        }

        @Test
        @DisplayName("Should not divide by zero")
        public void divide_by_zero_should_throw_exception() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When / Then
            assertThatThrownBy(() -> calc.division(10.0, 0.0))
                    .isInstanceOf(ArithmeticException.class)
                    .hasMessage("Division by zero is not allowed");
        }

        @Test
        @DisplayName("Should not divide negative numbers")
        public void divide_negative_numbers_should_throw_exception() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When / Then
            assertThatThrownBy(() -> calc.division(-10.0, 2.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Negative numbers are not allowed");
        }

        @Test
        @DisplayName("result of division should be rounded to 2 decimal places")
        public void divide_10_by_3_should_return_3_33() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When
            double res = calc.division(10.0, 3.0);

            // Then
            assert(res == 3.33);
        }
        @Test
        @DisplayName("Should return erreur count > 100")
        public void maximum_100_operation_should_return_erreur() {
            // Given
            Calculatrice calc = new Calculatrice();
            calc.count = 100;

            // Then
            assertThatThrownBy(() -> calc.division(10, 2)).isInstanceOf(IllegalArgumentException.class).hasMessage("Reach the limit of 100 iteration");
            assertEquals(100, calc.count);
        }

        @Test
        @DisplayName("Should return erreur if count = 100")
        public void maximum_100_operation_should_return_ok() {
            // Given
            Calculatrice calc = new Calculatrice();

            // When
            calc.count = 99;
            double result = calc.division(10.0, 2.0);
            // Then
            assertEquals(100, calc.count);
            assert (result == 5);
        }
    }

}