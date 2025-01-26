package com.example.popcorn.DTOs;

import com.example.popcorn.Models.CastMember;
import com.example.popcorn.Models.CrewMember;
import com.example.popcorn.Models.Person;

import java.util.List;

public class CreditsResponse {
    private List<CastMember> cast;
    private List<CrewMember> crew;

    public List<CastMember> getCast() {
        return cast;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }
}