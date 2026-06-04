package io.rapa.backendcrossing.npcs.service;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcService
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : Npc 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.response.NpcsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NpcService {

    private final NpcsRepository npcsRepository;

    public List<NpcsResponse> findAllNpcs() {
        return npcsRepository.findAll().stream()
                .map(NpcsResponse::from)
                .toList();
    }

    public NpcsResponse getNpcInfo(Long npcId) {
        return NpcsResponse.from(npcsRepository.findByIdOrThrow(npcId));
    }
}
