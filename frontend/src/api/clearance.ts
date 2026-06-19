import request from '@/utils/request';
import type { FodClearance, ClearanceOperationDTO, Result } from '@/types';

export const clearanceApi = {
  operate: (data: ClearanceOperationDTO): Promise<Result<FodClearance>> => {
    return request.post('/clearance/operate', data);
  },

  getByEventId: (eventId: number): Promise<Result<FodClearance[]>> => {
    return request.get(`/clearance/event/${eventId}`);
  },

  getByRunwayId: (runwayId: number): Promise<Result<FodClearance[]>> => {
    return request.get(`/clearance/runway/${runwayId}`);
  },
};
