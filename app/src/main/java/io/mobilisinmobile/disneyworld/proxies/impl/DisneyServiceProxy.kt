package io.mobilisinmobile.disneyworld.proxies.impl

import io.mobilisinmobile.disneyworld.DisneyService
import io.mobilisinmobile.disneyworld.RestCharacterResult
import io.mobilisinmobile.disneyworld.RestCharactersResult
import io.mobilisinmobile.disneyworld.proxies.definition.IDisneyServiceProxy

class DisneyServiceProxy(private val disneyService: DisneyService) : IDisneyServiceProxy {

    override suspend fun getCharacter(characterId: Int): RestCharacterResult {
        return disneyService.getCharacter(characterId)
    }

    override suspend fun getCharacters(): RestCharactersResult {
        return disneyService.getCharacters()
    }
}