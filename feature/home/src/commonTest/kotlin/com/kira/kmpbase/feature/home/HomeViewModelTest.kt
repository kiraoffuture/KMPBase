package com.kira.kmpbase.feature.home

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact
import com.kira.kmpbase.core.domain.repository.HomeRepository
import com.kira.kmpbase.core.domain.usecase.home.ObserveContactsUseCase
import com.kira.kmpbase.core.domain.usecase.home.RefreshContactsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadContacts_emitsSuccessState() = runTest {
        val contacts = listOf(Contact(1, "Alice", "+84123456789"))
        val repository = FakeHomeRepository(flowOf(AppResult.Success(contacts)))
        val viewModel = createViewModel(repository)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(contacts, state.contacts)
        assertEquals(null, state.error)
    }

    @Test
    fun refresh_updatesContactsOnSuccess() = runTest {
        val contacts = listOf(Contact(2, "Bob", "+84987654321"))
        val repository = FakeHomeRepository(
            flow = flowOf(AppResult.Success(emptyList())),
            refreshResult = AppResult.Success(contacts),
        )
        val viewModel = createViewModel(repository)

        viewModel.refresh()

        val state = viewModel.uiState.value
        assertEquals(contacts, state.contacts)
        assertFalse(state.isLoading)
    }

    @Test
    fun refresh_setsErrorOnFailure() = runTest {
        val repository = FakeHomeRepository(
            flow = flowOf(AppResult.Success(emptyList())),
            refreshResult = AppResult.Error(AppError.Network("Network down")),
        )
        val viewModel = createViewModel(repository)

        viewModel.refresh()

        val state = viewModel.uiState.value
        assertEquals(AppError.Network("Network down"), state.error)
        assertTrue(state.contacts.isEmpty())
    }

    private fun createViewModel(repository: HomeRepository): HomeViewModel {
        return HomeViewModel(
            observeContactsUseCase = ObserveContactsUseCase(repository),
            refreshContactsUseCase = RefreshContactsUseCase(repository),
        )
    }

    private class FakeHomeRepository(
        private val flow: Flow<AppResult<List<Contact>>>,
        private val refreshResult: AppResult<List<Contact>> = AppResult.Success(emptyList()),
    ) : HomeRepository {
        override fun observeContacts(): Flow<AppResult<List<Contact>>> = flow
        override suspend fun refreshContacts(): AppResult<List<Contact>> = refreshResult
    }
}
