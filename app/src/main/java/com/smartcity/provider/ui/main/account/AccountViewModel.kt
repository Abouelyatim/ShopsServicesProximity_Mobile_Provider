package com.smartcity.provider.ui.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.main.AccountRepository
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseViewModel
import com.smartcity.provider.ui.DataState
import com.smartcity.provider.ui.Loading
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import javax.inject.Inject

@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
)
    : BaseViewModel<AccountStateEvent, AccountViewState>()
{

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is AccountStateEvent.None ->{
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun cancelActiveJobs(){
        accountRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(AccountStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}