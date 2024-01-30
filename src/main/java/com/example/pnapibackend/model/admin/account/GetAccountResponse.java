package com.example.pnapibackend.model.admin.account;

import java.util.List;

public record GetAccountResponse(List<AccountResponse> accounts, int pageNumber) {
}
