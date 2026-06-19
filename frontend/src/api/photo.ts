import request from '@/utils/request';
import type { FodPhoto, Result } from '@/types';

export const photoApi = {
  upload: (
    eventId: number,
    files: File[],
    photoType?: number,
    uploaderId?: string,
    uploaderName?: string,
    description?: string
  ): Promise<Result<FodPhoto[]>> => {
    const formData = new FormData();
    formData.append('eventId', eventId.toString());
    if (photoType) formData.append('photoType', photoType.toString());
    if (uploaderId) formData.append('uploaderId', uploaderId);
    if (uploaderName) formData.append('uploaderName', uploaderName);
    if (description) formData.append('description', description);
    files.forEach((file) => formData.append('files', file));

    return request.post('/photo/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  getByEventId: (eventId: number): Promise<Result<FodPhoto[]>> => {
    return request.get(`/photo/event/${eventId}`);
  },

  delete: (id: number, operatorId?: string): Promise<Result<boolean>> => {
    return request.delete(`/photo/${id}`, { params: { operatorId } });
  },
};
