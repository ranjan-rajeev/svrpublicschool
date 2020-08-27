package com.svrpublicschool.splash;

import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.svrpublicschool.R;
import com.svrpublicschool.SVRApplication;
import com.svrpublicschool.SplashScreenActivity;
import com.svrpublicschool.mockserver.MockServerRule;
import com.svrpublicschool.mockserver.RestServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SplashScreenActivityTest {

    FakeAppUpdateManager fakeAppUpdateManager;

    @Rule
    public ActivityTestRule<SplashScreenActivity> activityTestRule = new ActivityTestRule<>(SplashScreenActivity.class, true, false);

    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fakeAppUpdateManager = new FakeAppUpdateManager(SVRApplication.getContext());
    }

/*    @Test
    public void testFlexibleUpdate_Completes() {
        // Setup flexible update.
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.FLEXIBLE ;
        fakeAppUpdateManager.setUpdateAvailable(2);

        ActivityScenario.launch(MainActivity::class.java)

        // Validate that flexible update is prompted to the user.
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        // Simulate user's and download behavior.
        fakeAppUpdateManager.userAcceptsUpdate()

        fakeAppUpdateManager.downloadStarts()

        fakeAppUpdateManager.downloadCompletes()

        // Perform a click on the Snackbar to complete the update process.
        onView(
                allOf(
                        isDescendantOfA(instanceOf(Snackbar.SnackbarLayout::class.java)),
        instanceOf(AppCompatButton::class.java)
            )
        ).perform(ViewActions.click())

        // Validate that update is completed and app is restarted.
        assertTrue(fakeAppUpdateManager.isInstallSplashScreenVisible)

        fakeAppUpdateManager.installCompletes()
    }

    @Test
    fun testImmediateUpdate_Completes() {
        // Setup immediate update.
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.IMMEDIATE
        fakeAppUpdateManager.setUpdateAvailable(2)

        ActivityScenario.launch(MainActivity::class.java)

        // Validate that immediate update is prompted to the user.
        assertTrue(fakeAppUpdateManager.isImmediateFlowVisible)

        // Simulate user's and download behavior.
        fakeAppUpdateManager.userAcceptsUpdate()

        fakeAppUpdateManager.downloadStarts()

        fakeAppUpdateManager.downloadCompletes()

        // Validate that update is completed and app is restarted.
        assertTrue(fakeAppUpdateManager.isInstallSplashScreenVisible)
    }

    @Test
    fun testFlexibleUpdate_DownloadFails() {
        // Setup flexible update.
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.FLEXIBLE
        fakeAppUpdateManager.setUpdateAvailable(2)

        ActivityScenario.launch(MainActivity::class.java)

        // Validate that flexible update is prompted to the user.
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        // Simulate user's and download behavior.
        fakeAppUpdateManager.userAcceptsUpdate()

        fakeAppUpdateManager.downloadStarts()

        fakeAppUpdateManager.downloadFails()

        // Perform a click on the Snackbar to retry the update process.
        onView(
                allOf(
                        isDescendantOfA(instanceOf(Snackbar.SnackbarLayout::class.java)),
        instanceOf(AppCompatButton::class.java)
            )
        ).perform(ViewActions.click())

        // Validate that update is not completed and app is not restarted.
        assertFalse(fakeAppUpdateManager.isInstallSplashScreenVisible)

        // Validate that Flexible update is prompted to the user again.
        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)
    }

    @After
    public void tearDown() {
        activityTestRule.getActivity().finishAndRemoveTask();
        mMockServerRule.stopServer();
    }

    private Dispatcher getActionsApiDispatcher(int type) {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {
                //return new MockResponse().setResponseCode(200).setBody("{}");
                String apiName = Uri.parse(recordedRequest.getRequestUrl().toString()).getLastPathSegment();
                switch (apiName) {
                    case "getCliqCashPageActions": {
                        if (type == 1) {
                            return new MockResponse().setResponseCode(200).setBody(RestServiceTestHelper.readJsonFile(CliqApplication.getContext(), "response/cliqcash/getCliqCashPageActions_success.json"));
                        } else if (type == 2) {
                            return new MockResponse().setResponseCode(200).setBody(RestServiceTestHelper.readJsonFile(CliqApplication.getContext(), "response/cliqcash/getCliqCashPageActions_failure.json"));
                        }
                    }
                }
                return new MockResponse().setResponseCode(404).setBody("Url Not found");
            }
        };
    }*/
}
