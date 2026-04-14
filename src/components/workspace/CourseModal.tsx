'use client';

import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import { Course, CourseGroup } from '@/types/workspace/WorkspaceType';
import {CourseGroupType} from "@/types/coursegroup/CourseGroupType";

interface CourseModalProps {
  visible: boolean;
  mode: 'create' | 'edit';
  course?: Course | null;
  courseGroups: CourseGroupType[];
  loading?: boolean;
  onCancel: () => void;
  onSubmit: (values: { repoCategoryName: string; courseGroupId: number; teacherUserId: number; }) => Promise<void>;
}

const CourseModal: React.FC<CourseModalProps> = ({
  visible,
  mode,
  course,
  courseGroups,
  loading = false,
  onCancel,
  onSubmit,
}) => {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (visible) {
      if (mode === 'edit' && course) {
        // 编辑模式：填充现有数据
        const group = courseGroups.find((g) => g.courseGroupName === course.courseGroupName);
        form.setFieldsValue({
          repoCategoryName: course.repoCategoryName,
          courseGroupId: group?.id || '',
        });
      } else {
        // 创建模式：重置表单
        form.resetFields();
      }
    }
  }, [visible, mode, course, courseGroups, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      await onSubmit(values);
      form.resetFields();
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title={mode === 'create' ? '创建新课程' : '修改课程'}
      open={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={submitting || loading}
      okText={mode === 'create' ? '创建' : '保存'}
      cancelText="取消"
      destroyOnHidden
      styles={{
        body: { paddingTop: 24 },
      }}
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        <Form.Item
          name="repoCategoryName"
          label="课程名称"
          rules={[
            { required: true, message: '请输入课程名称' },
            { max: 50, message: '课程名称不能超过50个字符' },
          ]}
        >
          <Input
            placeholder="请输入课程名称"
            size="large"
            style={{ borderRadius: 8 }}
          />
        </Form.Item>

        <Form.Item
          name="courseGroupId"
          label="所属课程组"
          rules={[{ required: true, message: '请选择所属课程组' }]}
        >
          <Select
            placeholder="请选择所属课程组"
            size="large"
            style={{ borderRadius: 8 }}
            options={courseGroups.map((group) => ({
              value: group.id,
              label: group.courseGroupName,
            }))}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CourseModal;
