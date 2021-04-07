package com.smartcity.provider.repository.main

import com.smartcity.provider.api.main.OpenApiMainService
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.repository.JobManager
import com.smartcity.provider.session.SessionManager
import javax.inject.Inject

@MainScope
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
): JobManager("AccountRepository")
{
    private val TAG: String = "AppDebug"

}