package com.example.proyectofinalfct


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.anything
import androidx.test.espresso.action.ViewActions
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class InTimeTestExpresso {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun inTimeTestExpresso() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.txtUser),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("admin2@gmail.com "))

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.txtPwd),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("administrador"))

        val materialButton = onView(
            allOf(
                withId(R.id.btnLogin), withText("LOGIN"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())
        Thread.sleep(20000)
        val appCompatImageView = onView(
            allOf(
                withId(R.id.imgFichar),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnRegistro), withText("Registrar Jornada Online"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton2.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withId(com.firebase.ui.auth.R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton3.perform(scrollTo(), click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.btnQr), withText("Generar Qr"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        3
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton4.perform(click())

        val materialButton5 = onView(
            allOf(
                withId(R.id.btnVo), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        3
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton5.perform(click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.imgHextra),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView2.perform(click())

        val materialButton6 = onView(
            allOf(
                withId(R.id.btnVolv), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        2
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton6.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("1"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageButton.perform(click())

        val navigationMenuItemView = onView(
            allOf(
                withId(R.id.opDatos),
                childAtPosition(
                    allOf(
                        withId(com.firebase.ui.auth.R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navigation_view),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        navigationMenuItemView.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.txtName), withText("text"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("tttttt"))

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.txtName), withText("tttttt"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform()

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.txtApe), withText("text"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("yyyyy"))

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.txtApe), withText("yyyyy"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform()

        val materialButton7 = onView(
            allOf(
                withId(R.id.btnSfoto), withText("Subir Imagen"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        6
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton7.perform(click())

        val materialButton8 = onView(
            allOf(
                withId(R.id.btnAcept), withText("Guardar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        7
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton8.perform(click())

        val appCompatImageView3 = onView(
            allOf(
                withId(R.id.imgVaca),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView3.perform(click())

        val materialCheckBox = onView(
            allOf(
                withId(R.id.chVacaciones), withText("Vacaciones"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialCheckBox.perform(click())

        val materialButton9 = onView(
            allOf(
                withId(R.id.btnFini), withText("Seleccionar D�as"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton9.perform(click())

        val materialButton10 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton10.perform(scrollTo(), click())

        val materialButton11 = onView(
            allOf(
                withId(R.id.btnFFin), withText("Seleccionar D�as"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton11.perform(click())

        val materialButton12 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton12.perform(scrollTo(), click())

        val materialButton13 = onView(
            allOf(
                withId(R.id.btnSave), withText("Enviar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        2
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton13.perform(click())

        val materialButton14 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withId(com.firebase.ui.auth.R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton14.perform(scrollTo(), click())

        val materialButton15 = onView(
            allOf(
                withId(R.id.btnV), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        2
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton15.perform(click())

        val appCompatImageView4 = onView(
            allOf(
                withId(R.id.imgMedico),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView4.perform(click())

        val materialButton16 = onView(
            allOf(
                withId(R.id.btnFechaIni), withText("Seleccionar D�as"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout4),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton16.perform(click())

        val materialButton17 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton17.perform(scrollTo(), click())

        val materialButton18 = onView(
            allOf(
                withId(R.id.btnFechaFin), withText("Seleccionar D�as"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout4),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton18.perform(click())

        val materialButton19 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton19.perform(scrollTo(), click())

        val materialButton20 = onView(
            allOf(
                withId(R.id.btnSubir), withText("Subir Documento"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.drawer_layout),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton20.perform(click())

        val materialButton21 = onView(
            allOf(
                withId(R.id.btnAcp), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.drawer_layout),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton21.perform(click())

        val materialButton22 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aceptar"),
                childAtPosition(
                    childAtPosition(
                        withId(com.firebase.ui.auth.R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton22.perform(scrollTo(), click())

        val materialButton23 = onView(
            allOf(
                withId(R.id.btnVol), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.drawer_layout),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton23.perform(click())

        val appCompatImageView5 = onView(
            allOf(
                withId(R.id.imgNotifi),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView5.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.rvNotifi),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    2
                )
            )
        )
        Thread.sleep(1000)
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val materialButton24 = onView(
            allOf(
                withId(android.R.id.button1), withText("Aprobar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        Thread.sleep(1000)
        materialButton24.perform(scrollTo(), click())

        val materialButton25 = onView(
            allOf(
                withId(R.id.btnB), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.drawer_layout),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton25.perform(click())

        val appCompatImageView6 = onView(
            allOf(
                withId(R.id.imgCalendario),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout3),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageView6.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withContentDescription("1"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        appCompatImageButton2.perform(click())

        val navigationMenuItemView2 = onView(
            allOf(
                withId(R.id.opDatos),
                childAtPosition(
                    allOf(
                        withId(com.firebase.ui.auth.R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navigation_view),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        navigationMenuItemView2.perform(click())

        val materialButton26 = onView(
            allOf(
                withId(R.id.btnVolver), withText("Volver"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        7
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton26.perform(click())

        val materialButton27 = onView(
            allOf(
                withId(R.id.btnClose), withText("Salir"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.drawer_layout),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        Thread.sleep(1000)
        materialButton27.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
